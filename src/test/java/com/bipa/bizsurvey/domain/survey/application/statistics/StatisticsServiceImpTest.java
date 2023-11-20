package com.bipa.bizsurvey.domain.survey.application.statistics;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.response.ChartAndTextResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.FileResultResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.StatisticsResponse;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class StatisticsServiceImpTest {

    @Autowired
    private StatisticsServiceImp serviceImp;
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

        Long surveyId = 24L;
        Long postId = 1L;
        String type = "";

        StatisticsResponse result = serviceImp.getPostResult(surveyId, postId, type);

        log.info("===========!!!!!!!!!!!!!!!!!");

        List<ChartAndTextResponse> c = result.getChartAndTextResults();

        c.forEach(dto -> {
            log.info("dto={}", dto);
        });

        result.getFileResults().forEach(dto -> {
            log.info("dto = {}", dto);
        });

    }

}