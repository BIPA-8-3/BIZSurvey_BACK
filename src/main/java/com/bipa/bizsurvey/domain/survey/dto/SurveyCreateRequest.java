package com.bipa.bizsurvey.domain.survey.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreateRequest {

    private String title;

    private String content;

    private List<?> questions;

}
