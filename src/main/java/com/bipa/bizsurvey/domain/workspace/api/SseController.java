package com.bipa.bizsurvey.domain.workspace.api;

import com.bipa.bizsurvey.domain.workspace.application.SseEmitters;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/sse")
@Log4j2
public class SseController {
    private final SseEmitters sseEmitters;

    public SseController(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestParam Long userId) {
        SseEmitter emitter = sseEmitters.add(userId);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(emitter);
    }
}