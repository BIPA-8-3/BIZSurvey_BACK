package com.bipa.bizsurvey.domain.workspace.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequestDto {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String keyword;

}
