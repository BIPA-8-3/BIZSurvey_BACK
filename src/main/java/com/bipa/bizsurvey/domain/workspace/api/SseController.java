package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@Log4j2
public class SseController {
    private final SseEmitters sseEmitters;

    public SseController(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @GetMapping(value ="/connect/{workspaceId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@PathVariable Long workspaceId) {
        SseEmitter emitter = new SseEmitter(60 * 5000L);
        sseEmitters.add(workspaceId, emitter);

        // 생성 이후 아무런 데이터도 보내지 않으면 재연결 요칭시 503 에러 발생 가능성 존재, 처음 SSE 연결 시 더메 데이터 전달
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

    @GetMapping(value="/acceptInvite/{workspaceId}")
    public ResponseEntity<Void> accept(@PathVariable Long workspaceId) {
        sseEmitters.acceptInvite(workspaceId);
        return ResponseEntity.ok().build();
    }
}
