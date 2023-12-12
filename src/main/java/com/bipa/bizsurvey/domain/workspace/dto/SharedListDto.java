package com.bipa.bizsurvey.domain.workspace.dto;

import com.bipa.bizsurvey.domain.workspace.domain.SharedSurvey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SharedListDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long sharedSurveyId;
        private Long contactId;
        private String email;
        private String name;
        private Long response;
    }
}
