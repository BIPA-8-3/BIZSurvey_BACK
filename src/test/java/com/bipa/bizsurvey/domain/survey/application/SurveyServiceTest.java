package com.bipa.bizsurvey.domain.survey.application;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
@Transactional
class SurveyServiceTest {


    @Autowired
    private SurveyService surveyService;

    @Test
    public void testGetSurvey(){

        Long id = 30L;

        surveyService.getSurvey(id);


    }


}