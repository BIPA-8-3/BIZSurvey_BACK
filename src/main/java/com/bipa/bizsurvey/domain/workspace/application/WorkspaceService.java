package com.bipa.bizsurvey.domain.workspace.application;


import com.bipa.bizsurvey.domain.survey.domain.QAnswer;
import com.bipa.bizsurvey.domain.survey.domain.QQuestion;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.request.UpdateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.user.domain.QUser;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.domain.QWorkspace;
import com.bipa.bizsurvey.domain.workspace.domain.QWorkspaceAdmin;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.domain.WorkspaceAdmin;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceDto;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final WorkspaceAdminRepository workspaceAdminRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public WorkspaceDto.ListResponse create(Long userId, WorkspaceDto.CreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));

        if (request.getWorkspaceType() == null) {
            request.setWorkspaceType(WorkspaceType.COMPANY);
        }
        Workspace workspace = Workspace.builder()
                .workspaceName(request.getWorkspaceName())
                .workspaceType(request.getWorkspaceType())
                .user(user)
                .build();

        workspaceRepository.save(workspace);

        return WorkspaceDto.ListResponse.builder()
                .workspaceName(workspace.getWorkspaceName())
                .workspaceType(workspace.getWorkspaceType())
                .id(workspace.getId())
                .build();
    }

//    @Transactional(readOnly = true)
//    public List<WorkspaceDto.ListResponse> listWorkspaces(LoginUser loginUser) {
//        Long userId = loginUser.getId();
//        List<Workspace> list = workspaceRepository.findWorkspaceByDelFlagFalseAndUserId(userId);
//        return list.stream().map(e ->
//                WorkspaceDto.ListResponse.builder()
//                        .workspaceName(e.getWorkspaceName())
//                        .workspaceType(e.getWorkspaceType())
//                        .id(e.getId()).build()).collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<WorkspaceDto.ListResponse> listWorkspaces(LoginUser loginUser) {
        List<Workspace> list = null;

        Plan plan = Plan.valueOf(loginUser.getPlan());
        Long userId = loginUser.getId();

        switch (plan) {
            case COMPANY_SUBSCRIBE:
                list = workspaceRepository.findCompanyPlanWorkspaces(userId);
                break;
            case NORMAL_SUBSCRIBE:
                list = workspaceRepository.findPersonalPlanWorkspaces(userId, WorkspaceType.PERSONAL);
                break;
            case COMMUNITY:
                list = workspaceRepository.findCommunityPlanWorkspaces(userId);
                break;
        }

        return list.stream().map(e ->
                WorkspaceDto.ListResponse.builder()
                        .workspaceName(e.getWorkspaceName())
                        .workspaceType(e.getWorkspaceType())
                        .id(e.getId()).build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkspaceDto.ReadResponse readOne(Long id) {
        Workspace workspace = getWorkspace(id);

        WorkspaceDto.ReadResponse response = WorkspaceDto.ReadResponse.builder()
                .id(workspace.getId())
                .workspaceType(workspace.getWorkspaceType())
                .workspaceName(workspace.getWorkspaceName())
                .email(workspace.getUser().getEmail())
                .regDate(workspace.getRegDate())
                .modDate(workspace.getModDate())
                .build();

        return response;
    }

    public void update(Long workspaceId, WorkspaceDto.UpdateRequest request) {
        Workspace workspace = getWorkspace(workspaceId);
        workspace.updateWorkspace(request.getWorkspaceName());
    }

    public void delete(Long workspaceId) {
        Workspace workspace = getWorkspace(workspaceId);
        workspace.delete();
    }

    private Workspace getWorkspace(Long id) {
        return workspaceRepository.findWorkspaceByIdAndDelFlagFalse(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다."));
    }


    public void updateSurveyName(WorkspaceDto.UpdateSurveyTitle request) {
        Survey survey = surveyRepository.findByIdAndDelFlagFalse(request.getSurveyId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 설문지 입니다."));
        survey.updateTitle(request.getTitle());
    }

    public void getPersonalWorkspace(Long userId) {
        workspaceRepository.findByDelFlagFalseAndUserIdAndWorkspaceType(userId, WorkspaceType.PERSONAL).orElseThrow(() -> new EntityNotFoundException("개인 워크스페이스가 존재하지 않습니다."));
    }

    public boolean permissionCheck(Long userId, Plan plan) {
        boolean permission = false;

        List<WorkspaceAdmin> adminList = workspaceAdminRepository.findByUserIdAndDelFlagFalse(userId);
        List<User> userList = adminList.stream().map(e -> e.getWorkspace().getUser()).collect(Collectors.toList());
        QUser user = QUser.user;

        // 관리자로 초대된 워크스페이스가 존재하는지 확인
        if (adminList != null && adminList.size() > 0) {
            permission = jpaQueryFactory
                    .select()
                    .from(user)
                    .where(
                            user.delFlag.isFalse()
                                    .and(user.planSubscribe.eq(Plan.COMPANY_SUBSCRIBE))
                                    .and(user.in(userList))
                    )
                    .fetchCount() > 0;

            if (permission) {
                return permission;
            }
        }
        // 내 명의의 워크스페이스가 존재하는지 확인
        List<Workspace> list = workspaceRepository.findWorkspacesByUserIdAndDelFlagFalse(userId);

        if (!Plan.COMMUNITY.equals(plan) && list != null && list.size() > 0) {
            permission = true;
        }
        return permission;
    }
}

