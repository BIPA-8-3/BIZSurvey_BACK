package com.bipa.bizsurvey.domain.survey.application.statistics;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.application.StatisticsService;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.response.*;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Slf4j
@Transactional
class StatisticsServiceImpTest {

    @Autowired
    private StatisticsService serviceImp;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private SurveyPostRepository surveyPostRepository;

    @Test
    public void testChart(){

        Survey survey = surveyRepository.findById(24L).orElseThrow();
        SurveyPost surveyPost = surveyPostRepository.findById(1L).orElseThrow();

        List<ChartAndTextResponse> results  =serviceImp.processChartAndText(survey, surveyPost);

        results.forEach(dto -> {
            log.info("dto = {}", dto);
        });
    }


    @Test
    public void testFile(){
        Survey survey = surveyRepository.findById(24L).orElseThrow();
        SurveyPost surveyPost = surveyPostRepository.findById(1L).orElseThrow();

        List<FileResultResponse> results = serviceImp.processFile(survey, surveyPost);

        results.forEach(dto -> {
            log.info("dto = {}", dto);
        });
    }

    @Test
    public void testGetResultStatistics(){

        Long postId = 1L;

        StatisticsResponse result = serviceImp.getPostResult(postId);

        List<ChartAndTextResponse> c = result.getChartAndTextResults();

        c.forEach(dto -> {
            log.info("dto={}", dto);
        });

        result.getFileResults().forEach(dto -> {
            log.info("dto = {}", dto);
        });

    }

    @Test
    public void testScoreResult(){

        Long surveyId = 25L;
        Long postId = 2L;

        List<ScoreResultResponse> result = serviceImp.getScoreResult(surveyId, postId);

        result.forEach(dto-> {
            log.info("dto ={}", dto);
        });

    }


}