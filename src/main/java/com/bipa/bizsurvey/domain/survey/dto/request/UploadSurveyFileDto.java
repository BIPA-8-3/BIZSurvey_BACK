package com.bipa.bizsurvey.domain.survey.dto.request;

import com.bipa.bizsurvey.global.common.storage.Domain;
import com.bipa.bizsurvey.global.common.storage.ShareType;
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
        return String.format("%s%d/%s%d/%d/", Domain.SURVEY.getDomainName(), surveyId, shareType.getType(),shareId, questionId);
    }
}

