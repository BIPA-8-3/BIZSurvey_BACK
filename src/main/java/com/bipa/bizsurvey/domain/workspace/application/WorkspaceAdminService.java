package com.bipa.bizsurvey.domain.workspace.application;


import com.bipa.bizsurvey.domain.user.domain.QUser;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.domain.QWorkspace;
import com.bipa.bizsurvey.domain.workspace.domain.QWorkspaceAdmin;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.domain.WorkspaceAdmin;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import com.bipa.bizsurvey.domain.workspace.enums.AdminType;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.common.email.EmailMessage;
import com.bipa.bizsurvey.global.common.email.MailUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Log4j2
@Service
@RequiredArgsConstructor
public class WorkspaceAdminService {
    private final WorkspaceAdminRepository workspaceAdminRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JPAQueryFactory jpaQueryFactory;

    private final MailUtil mailUtil;
    private static final String TOKEN_PREFIX = "INVITE-";
    private static final Long TOKEN_VALID_TIME_SECONDS = 60 * 60 * 24 * 3L;

    @Value("${spring.domain.backend}")
    private String backendAddress;

    @Value("${spring.domain.frontend}")
    private String frontendAddress;

    public WorkspaceAdminDto.Response invite(WorkspaceAdminDto.InviteRequest request) throws Exception {
        Workspace workspace = workspaceRepository.findWorkspaceByIdAndDelFlagFalse(request.getWorkspaceId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다."));

        invitationCheck(request.getWorkspaceId(), request.getEmail());
        String token = generateInvitationToken();

        WorkspaceAdmin workspaceAdmin = WorkspaceAdmin.builder()
                .workspace(workspace)
                .remark(request.getEmail())
                .adminType(request.getAdminType())
                .token(token)
                .build();

        workspaceAdminRepository.save(workspaceAdmin);

        return processInvite(workspaceAdmin);
    }

    public WorkspaceAdminDto.Response reinvite(WorkspaceAdminDto.ReInviteRequest request) throws Exception {
        WorkspaceAdmin workspaceAdmin = getWorkspaceAdmin(request.getId());
        workspaceAdmin.updateToken(generateInvitationToken());

        workspaceAdminRepository.save(workspaceAdmin);

        return processInvite(workspaceAdmin);
    }

    private WorkspaceAdminDto.Response processInvite(WorkspaceAdmin workspaceAdmin) throws Exception {
        // send Email
        Workspace workspace = workspaceAdmin.getWorkspace();
        User owner = workspace.getUser();
        String token = workspaceAdmin.getToken();

        String subject = String.format("%s %s 님께서 %s 워크스페이스 관리자로 초대합니다.",
                "[BIZSURVEY]", owner.getNickname(), "[" + workspace.getWorkspaceName() + "]");

        Long adminId = workspaceAdmin.getId();
        String email = workspaceAdmin.getRemark();

        EmailMessage emailMessage = EmailMessage.builder()
                .to(email)
                .subject(subject)
                .build();

        String fullToken = adminId + "_" + token;

        emailMessage.put("msg", "초대를 수락하신다면 다음 링크를 눌러주세요. (링크는 3일간 유효합니다.)");
        emailMessage.put("hasLink", true);
        emailMessage.put("link", frontendAddress + "/authorization/invite/" + fullToken);
        emailMessage.put("linkText", "입장하기");

        mailUtil.sendTemplateMail(emailMessage);

        // redis
        redisService.saveData(TOKEN_PREFIX + fullToken, workspace.getId(), TOKEN_VALID_TIME_SECONDS);

        return WorkspaceAdminDto.Response.builder()
                .id(adminId)
                .workspaceId(workspace.getId())
                .email(email)
                .name(email)
                .nickName(email)
                .adminType(workspaceAdmin.getAdminType())
                .inviteFlag(false)
                .hasToken(true)
                .build();
    }

    public WorkspaceAdminDto.Response acceptInvite(WorkspaceAdminDto.AcceptRequest request) {
        String fullToken = TOKEN_PREFIX + request.getToken();

        if(redisService.validateDataExists(fullToken)) {
            throw new RuntimeException("유효하지 않은 key값 입니다.");
        }

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
        Long adminId = Long.parseLong(request.getToken().split("_")[0]);
        WorkspaceAdmin workspaceAdmin = getWorkspaceAdmin(adminId);

        workspaceAdmin.acceptInvite(user);
        Long workspaceId = Long.parseLong(redisService.getData(fullToken));
        redisService.deleteData(fullToken);

        return WorkspaceAdminDto.Response.builder()
                .id(workspaceAdmin.getId())
                .workspaceId(workspaceId)
                .userId(user.getId())
                .profileUrl(null)
                .email(user.getEmail())
                .name(user.getName())
                .nickName(user.getNickname())
                .adminType(workspaceAdmin.getAdminType())
                .inviteFlag(workspaceAdmin.getInviteFlag())
                .build();
    }

    @Transactional(readOnly = true)
    public WorkspaceAdminDto.ListResponse list(Long workspaceId) {
        List<WorkspaceAdmin> list = workspaceAdminRepository.findByWorkspaceIdAndDelFlagFalse(workspaceId);

        List<WorkspaceAdminDto.Response> adminList = list.stream().filter(e -> e.getInviteFlag())
                .map(e -> WorkspaceAdminDto.Response.builder()
                        .id(e.getId())
                        .email(e.getUser().getEmail())
                        .name(e.getUser().getName())
                        .adminType(e.getAdminType())
                        .nickName(e.getUser().getNickname())
                        .inviteFlag(e.getInviteFlag())
                        .profileUrl(e.getUser().getProfile())
                        .build())
                .collect(Collectors.toList());

        List<WorkspaceAdminDto.Response> waitList = list.stream().filter(e -> !e.getInviteFlag())
                .map(e -> WorkspaceAdminDto.Response.builder()
                        .id(e.getId())
                        .email(e.getRemark())
                        .name(e.getRemark())
                        .adminType(e.getAdminType())
                        .nickName(e.getRemark())
                        .inviteFlag(e.getInviteFlag())
                        .hasToken(e.getToken() != null)
                        .profileUrl(null)
                        .build())
                .collect(Collectors.toList());

        QUser qUser = QUser.user;
        QWorkspace qWorkspace = QWorkspace.workspace;

        User u = jpaQueryFactory.select(qUser)
                .from(qUser)
                .where(qUser.eq(
                        JPAExpressions.select(qWorkspace.user).from(qWorkspace).where(qWorkspace.id.eq(workspaceId))
                )).fetchOne();

        WorkspaceAdminDto.Response owner = WorkspaceAdminDto.Response.builder()
                .id(0L)
                .userId(u.getId())
                .email(u.getEmail())
                .name(u.getName())
                .workspaceId(workspaceId)
                .inviteFlag(true)
                .adminType(AdminType.INVITE)
                .nickName(u.getNickname())
                .profileUrl(u.getProfile())
                .build();

        WorkspaceAdminDto.ListResponse response = WorkspaceAdminDto.ListResponse.builder()
                .owner(owner)
                .adminList(adminList)
                .waitList(waitList)
                .build();

        return response;
    }

    public void delete(Long id) {
        WorkspaceAdmin admin = getWorkspaceAdmin(id);
        redisService.deleteData(TOKEN_PREFIX + admin.getId() + "_" + admin.getToken());
        admin.expireToken();
        admin.delete();
    }

    public void expireToken(Long id) {
        WorkspaceAdmin admin = getWorkspaceAdmin(id);
        admin.expireToken();
    }

    private WorkspaceAdmin getWorkspaceAdmin(Long id) {
        return workspaceAdminRepository.findByIdAndDelFlagFalse(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 관리자 입니다."));
    }

    private String generateInvitationToken() {
        String inviteToken  = RandomStringUtils.randomAlphanumeric(8);
        return inviteToken;
    }

    private void invitationCheck(Long id, String email) throws IllegalAccessException {
        QWorkspaceAdmin qWorkspaceAdmin = QWorkspaceAdmin.workspaceAdmin;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(qWorkspaceAdmin.delFlag.eq(false))
                      .and(qWorkspaceAdmin.workspace.id.eq(id))
                      .and(qWorkspaceAdmin.user.email.equalsIgnoreCase(email)
                        .or(qWorkspaceAdmin.remark.equalsIgnoreCase(email))
                      );

        boolean exists = jpaQueryFactory.select(qWorkspaceAdmin)
                                            .from(qWorkspaceAdmin)
                                            .leftJoin(qWorkspaceAdmin.user).fetchJoin()
                                            .where(booleanBuilder)
                                            .fetchFirst() != null;

        if (exists) {
            log.error("User with email {} is already invited.", email);
            throw new IllegalAccessException("이미 초대된 메일입니다.");
        }
    }

    public void test() {
        String token = generateInvitationToken();
        Long validTime = Long.valueOf(10);
        redisService.saveData(TOKEN_PREFIX + token, "test", validTime);
        redisService.getRedisTemplate().convertAndSend("__keyevent@*__:expired", TOKEN_PREFIX + token);
    }
}
