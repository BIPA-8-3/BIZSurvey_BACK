package com.bipa.bizsurvey.infra.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
public class UploadSurveyFileDto {
    private MultipartFile file;
    private Long surveyId;
    private Long shareId;
    private ShareType shareType;
    private Long questionId;


    public String getPath() {
        return String.format("%s%d/%s%d/%d/", S3Domain.SURVEY.getDomainName(), surveyId, shareType.getType(),shareId, questionId);
    }
}

