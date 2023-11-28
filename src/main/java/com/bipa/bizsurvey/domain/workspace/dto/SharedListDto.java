package com.bipa.bizsurvey.domain.workspace.dto;

import com.bipa.bizsurvey.domain.workspace.domain.SharedSurvey;
import lombok.Builder;
import lombok.Data;

public class SharedListDto {

    @Data
    @Builder
    public static class Response {
        private Long id;
        private Long sharedSurveyId;
        private Long contactId;
        private String email;
        private String name;
    }
}
