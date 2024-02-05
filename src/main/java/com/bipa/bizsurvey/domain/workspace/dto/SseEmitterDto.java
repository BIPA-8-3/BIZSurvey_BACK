package com.bipa.bizsurvey.domain.workspace.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Data
@Builder
public class SseEmitterDto {
    private Long userId;
    private SseEmitter sseEmitter;
}
