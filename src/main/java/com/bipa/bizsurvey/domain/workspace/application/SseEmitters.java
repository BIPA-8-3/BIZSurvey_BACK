package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class SseEmitters {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap();
    private final WorkspaceAdminRepository workspaceAdminRepository;
    private final WorkspaceRepository workspaceRepository;

    // 다음 메소드에서 정의된 콜백은 SseEmitter를 관리하는 다른 스레드에서 실행됨
    // thread-safe 한 자료구조 사용 필요, 그렇지 않으면 ConcurrnetModificationException이 발생할 수 있음.
    // thread-safe한 자료구조인 CopyOnWriteArrayList를 사용함
    public SseEmitter add(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 5000L);
        emitters.put(userId, emitter);

        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            removeEmitter(userId, emitter); // 만료되면 리스트에서 삭제
        });

        // 타임아웃 발생 시 브라우저에서 재 연결 요청을 보냄, 이때 새로운 Emitter 객체를 다시 생성함. 기존에 Emiiter를 제거 하여야함
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    // 관리자 초대 이벤트 전송
    void acceptInvite(WorkspaceAdminDto.Response response) {
        List<SseEmitter> list = getEmitterList(response.getWorkspaceId());
        log.info("여기1: " + list.size());
        log.info(response.getWorkspaceId());

        list.forEach(emiiter -> {
            try {
                emiiter.send(SseEmitter.event()
                        .name("acceptInvite")
                        .data(response));

                log.info("다 보냇당");
            } catch (IOException e) {
                log.info("왜 에러임 ㅠㅠㅠ");
                throw new RuntimeException(e);
            }
        });
    }

    // 삭제 메소드
    private void removeEmitter(Long userId, SseEmitter emitter) {
        emitters.remove(userId, emitter);
        log.info("emitter removed: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
    }


    // 특정 워크스페이스에 속한 관리자들의 SseEmiiter 가져옴
    private List<SseEmitter> getEmitterList(Long workspaceId) {
        List<Long> userList = workspaceAdminRepository.findUserByWorkspaceId(workspaceId);
        log.info("userList.size(): " + userList.size());
        Optional<Workspace> workspace = workspaceRepository.findWorkspaceByIdAndDelFlagFalse(workspaceId);
        log.info("workspace: " + workspace.get().getWorkspaceName());
        Long owner = workspace.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다.")).getUser().getId();
        userList.add(owner);

        List<SseEmitter> sseEmiiterList = this.emitters.entrySet().stream().filter(entry -> userList.contains(entry.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());

        if (sseEmiiterList == null) {
            log.info("난 이제 널널널");
            sseEmiiterList = new CopyOnWriteArrayList<>();
        }

        log.info("끝남끝남");
        return sseEmiiterList;
    }
}
