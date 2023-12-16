package com.bipa.bizsurvey.domain.workspace.dto;

import com.bipa.bizsurvey.domain.survey.dto.response.ChartAndTextResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.FileResultResponse;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class SharedSurveyResponseDto {

    @Data
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long questionId;
        private AnswerType answerType;
        private String answer;
        private String url;
    }


    @Data
    @Builder
    public static class QuestionTotalResponse {
        private Long questionId;
        private String question;
        private AnswerType questionType;
        private List<String> answerList;
        private List<ChartInfo> chartInfo;
        private List<FileInfo> fileInfo;

//        private List<ChartAndTextResponse> chartAndTextResults = new ArrayList<>();
//        private List<com.bipa.bizsurvey.domain.survey.dto.response.FileResultResponse> fileResults = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    public static class QuestionResultResponse {
        private Long questionId;
        private List<String> answer;
    }

    @Data
    @AllArgsConstructor
    public static class ChartResultResponse {
        private Long questionId;
        private List<ChartInfo> chartInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileResultResponse {
        private Long questionId;
        private List<FileInfo> fileInfos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private String fileName;
        private String url;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartInfo {
        private String answer;
        private Long count;
    }



    // 점수형
    @Data
    @AllArgsConstructor
    public static class PersonalScoreSurveyResults {
        private Long questionId;
        private List<String> userAnswer;
    }

    @Data
    @AllArgsConstructor
    public static class ScoreQuestionInfo {
        private Long questionId;
        private String question;
        private List<String> correctAnswers;
        private Integer score;
    }

    @Data
    @AllArgsConstructor
    public static class ScoreQuestionResponse {
        private Long questionId;
        private List<String> answer;
    }
    
    @Data
    @AllArgsConstructor
    public static class ShareScoreResults {
        private Long questionId;
        private String question;
        private List<String> correctAnswer;
        private Integer score;
        private List<ShareScoreAnswer> answer;
        private Integer correctCnt;

        public ShareScoreResults(Long questionId, String question, List<String> correctAnswer, Integer score, List<ShareScoreAnswer> answer) {
            this.questionId = questionId;
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.score = score;
            this.answer = answer;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ShareScoreAnswer {
        private Long sharedListId;
        private List<String> question;
        private Integer score;

        public ShareScoreAnswer(Long sharedListId, List<String> question) {
            this.sharedListId = sharedListId;
            this.question = question;
        }
    }
}
