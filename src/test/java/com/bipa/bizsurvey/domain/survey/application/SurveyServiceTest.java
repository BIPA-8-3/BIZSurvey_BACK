package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.dto.survey.CreateAnswerRequest;
import com.bipa.bizsurvey.domain.survey.dto.survey.CreateQuestionRequest;
import com.bipa.bizsurvey.domain.survey.dto.survey.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.enums.Correct;
import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
//@Transactional
class SurveyServiceTest {

    @Autowired
    private SurveyService surveyService;

    @Test
    public void testCreateSurvey(){

        CreateSurveyRequest createSurveyRequest = new CreateSurveyRequest();
        createSurveyRequest.setTitle("titletest");
        createSurveyRequest.setContent("contenttest");
        createSurveyRequest.setSurveyType(SurveyType.SCORE);

        List<CreateQuestionRequest> qlist = new ArrayList<>();

        IntStream.rangeClosed(1, 5).forEach(i->{
            CreateQuestionRequest createQuestionRequest = new CreateQuestionRequest();
            createQuestionRequest.setSurveyQuestion("questiontest"+i);
            createQuestionRequest.setAnswerType(AnswerType.CALENDER);

            List<CreateAnswerRequest> alist = new ArrayList<>();

            IntStream.rangeClosed(1, 3).forEach(j->{
                CreateAnswerRequest createAnswerRequest = new CreateAnswerRequest();
                createAnswerRequest.setSurveyAnswer("answertest"+i);
                createAnswerRequest.setCorrect(Correct.NO);
                alist.add(createAnswerRequest);
            });

            createQuestionRequest.setAnswers(alist);

            qlist.add(createQuestionRequest);

        });


        createSurveyRequest.setQuestions(qlist);

        surveyService.createSurvey(createSurveyRequest);


    }

}