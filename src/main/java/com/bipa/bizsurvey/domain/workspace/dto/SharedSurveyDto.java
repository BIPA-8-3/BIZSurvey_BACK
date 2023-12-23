package com.bipa.bizsurvey.domain.workspace.dto;

import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
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
    public static class DeadlineRequest {
        private Long sharedSurveyId;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime deadlineDate;
    }

    @Data
    public static class SharedSurveyAnswerRequest {
        private Long surveyId;
        private String token;
        private List<SharedAnswer> sharedAnswerList;

        public void sortSharedAnswerListByQuestionId() {
            // questionId를 기준으로 정렬
            sharedAnswerList.sort(Comparator.comparing(SharedAnswer::getQuestionId));
        }
    }

    @Data
    public static class SharedAnswer {
        private Long questionId;
        private List<String> surveyAnswer;
        private AnswerType answerType;
        private String url;
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
