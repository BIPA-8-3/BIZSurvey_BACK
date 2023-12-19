package com.bipa.bizsurvey.domain.survey.dto.response;

import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResultResponse {

    //

    private Long questionId;

    //추가
    private String title;

    private AnswerType questionType;

    private List<FileInfo> fileInfos;

}
