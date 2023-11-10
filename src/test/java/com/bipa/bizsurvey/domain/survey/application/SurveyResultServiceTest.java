package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.dto.surveyresult.AnswerResponse;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.SurveyResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class SurveyResultServiceTest {

    @Autowired
    private SurveyResultService surveyResultService;

    @Test
    public void testGetSurveyResult(){

        SurveyResultResponse rs = surveyResultService.getSurveyResultInPost(1L);

        rs.getAnswerResponses().forEach(dto->{
            log.info("dto = {}", dto);
        });
    }

}