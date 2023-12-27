package com.bipa.bizsurvey.domain.workspace.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {
    private Long workspaceId;
    private String name;
    private Object response;
    private String errorMessage;
}
