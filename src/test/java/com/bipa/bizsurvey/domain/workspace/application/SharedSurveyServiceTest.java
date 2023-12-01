package com.bipa.bizsurvey.domain.workspace.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SharedSurveyServiceTest {

    @Autowired
    private SharedSurveyService sharedSurveyService;


    @Test
    public void test() {
        sharedSurveyService.readShareScoreResults(1L, 1L);
    };
}
