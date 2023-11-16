package com.bipa.bizsurvey.global.common.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
public class UploadSurveyAnswerRequest {
    private MultipartFile file;
    private Long surveyId;
    private Long shareId;
    private ShareType shareType;
    private Long questionId;
    private Domain domain;

    public String getPath() {
        return String.format("%d/%s-%d/%d/", surveyId, shareType.getType(), shareId, questionId);
    }
}