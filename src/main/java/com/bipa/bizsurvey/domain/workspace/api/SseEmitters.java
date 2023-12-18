package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.workspace.application.WorkspaceAdminService;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Log4j2
public class SseEmitters {
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap();
    private final WorkspaceAdminService workspaceAdminService;

    // 다음 메소드에서 정의된 콜백은 SseEmitter를 관리하는 다른 스레드에서 실행됨
    // thread-safe 한 자료구조 사용 필요, 그렇지 않으면 ConcurrnetModificationException이 발생할 수 있음.
    // thread-safe한 자료구조인 CopyOnWriteArrayList를 사용함
    SseEmitter add(Long workspaceId, SseEmitter emitter) {
        List<SseEmitter> list = getEmitterList(workspaceId);
        list.add(emitter);

        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", list.size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            removeEmitter(workspaceId, emitter); // 만료되면 리스트에서 삭제
        });

        // 타임아웃 발생 시 브라우저에서 재 연결 요청을 보냄, 이때 새로운 Emitter 객체를 다시 생성함. 기존에 Emiiter를 제거 하여야함
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    void acceptInvite(Long workspaceId) {
        WorkspaceAdminDto.ListResponse response = workspaceAdminService.list(workspaceId);
        List<SseEmitter> list = getEmitterList(workspaceId);

        list.forEach(emiiter -> {
            try {
                emiiter.send(SseEmitter.event()
                        .name("acceptInvite")
                        .data(response));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 삭제 메소드
    private void removeEmitter(Long workspaceId, SseEmitter emitter) {
        List<SseEmitter> list = this.emitters.get(workspaceId);

        if (list != null) {
            list.remove(emitter);
            log.info("emitter removed: {}", emitter);
            log.info("emitter list size: {}", list.size());
        }
    }

    private List<SseEmitter> getEmitterList(Long workspaceId) {
        List<SseEmitter> list = this.emitters.get(workspaceId);

        if(list == null) {
            list = new CopyOnWriteArrayList<>();
        }
        return list;
    }
}
