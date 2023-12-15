package com.bipa.bizsurvey.domain.workspace.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class SharedSurveyDto {
    @Data
    public static class SharedRequest {
        private Long surveyId;
        private Long deadline;
        private List<ContactDto.SharedRequest> contactList;
    }

    @Data
    public static class SharedSurveyAnswerResponse {
        private Long sharedSurveyId;
        private Long sharedListId;
        private List<SharedAnswer> sharedAnswerList;

        public void sortSharedAnswerListByQuestionId() {
            // questionId를 기준으로 정렬
            sharedAnswerList.sort(Comparator.comparing(SharedAnswer::getQuestionId));
        }
    }

    @Data
    public static class SharedAnswer {
        private Long questionId;
        private String surveyAnswer;
        private String url;
        private String filaName;
    }

    @Data
    @Builder
    public static class SharedSurveysResponse {
        private Long id;
        private LocalDateTime regDate;
        private LocalDateTime dueDate;
        private Long surveyId;
        private Boolean deadline;
    }
}
