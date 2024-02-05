package com.bipa.bizsurvey.domain.workspace.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {
    private Long workspaceId;
    private String name;
    private String detailEvent;
    private String errorMessage;
//    private Long userId;
    private Long id;
    @Builder.Default
    private boolean delFlag = false;

    //    private Object response;
}
