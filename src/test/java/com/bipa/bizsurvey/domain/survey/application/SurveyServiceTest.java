package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.dto.response.SurveyListResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SurveyServiceTest {

    @Autowired
    private SurveyService surveyService;

    @Test
    public void testSurveyList(){

        Long workspaceId = 1L;
        String fieldName = null;

        List<SurveyListResponse> list =  surveyService.getSurveyList(workspaceId, fieldName);

        list.forEach(dto -> {
            log.info("dto = {}", dto);
        });

    }

}