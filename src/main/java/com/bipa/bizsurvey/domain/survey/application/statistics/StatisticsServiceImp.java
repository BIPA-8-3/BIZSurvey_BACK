package com.bipa.bizsurvey.domain.survey.application.statistics;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.QUserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.response.*;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.Projections.list;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImp implements StatisticsService{

    private final SurveyPostRepository surveyPostRepository;
    private final QuestionRepository questionRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyRepository surveyRepository;

    public QUserSurveyResponse u = new QUserSurveyResponse("u");

    @Override
    public StatisticsResponse getPostResult(Long surveyId, Long postId, String type) {
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyPost.getSurvey();

        log.info(" surveyPost = {}", surveyPost);
        log.info("survey = {}", survey);

        List<ChartAndTextResponse> chartResults = new ArrayList<>();
        chartResults = processChartAndText(survey, surveyPost);
        List<FileResultResponse> fileResults = processFile(survey, surveyPost);

        log.info("서비스 들어옴.........");

        chartResults.forEach(dto->{
            log.info("dto ={}", dto);
        });
        fileResults.forEach(dto->{
            log.info("dto={}", dto);
        });

        StatisticsResponse result = new StatisticsResponse(chartResults, fileResults);
        return result;
    }



    public List<ChartAndTextResponse> processChartAndText(Survey survey, SurveyPost surveyPost){


        List<ChartAndTextResponse> results = jpaQueryFactory
                .select(Projections.constructor(ChartAndTextResponse.class, u.question.id, u.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(ChartAndTextResult.class, u.answer, u.count().as("count"))))
                )
                .from(u)
                .where(u.question.survey.eq(survey)
                        .and(u.question.delFlag.eq(false))
                        .and(u.surveyPost.eq(surveyPost))
                        .and(u.question.answerType.notIn(AnswerType.FILE))
                        .and(u.answerType.notIn(AnswerType.FILE)))
                .groupBy(u.question, u.answer)
                .fetch();

        // dto 형식에 맞게 변환
        return results.stream()
                .collect(Collectors.groupingBy(ChartAndTextResponse::getQuestionId))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long questionId = entry.getKey();
                    List<ChartAndTextResponse> chartResultResponses = entry.getValue();

                    // questionId에 대한 모든 answerType을 동일한 값으로 설정
                    AnswerType answerType = chartResultResponses.get(0).getQuestionType();

                    // 각 value(ChartResultResponse)의 answers를 합치기
                    List<ChartAndTextResult> combinedAnswers = chartResultResponses.stream()
                            .flatMap(chartResultResponse -> chartResultResponse.getAnswers().stream())
                            .collect(Collectors.toList());

                    // 새로운 ChartResultResponse 객체 생성
                    return new ChartAndTextResponse(questionId, answerType, combinedAnswers);
                })
                .collect(Collectors.toList());

    }




    public List<FileResultResponse> processFile(Survey survey, SurveyPost surveyPost){
        List<FileResultResponse> results = jpaQueryFactory
                .select(Projections.constructor(FileResultResponse.class, u.question.id, u.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(FileInfo.class, u.answer.as("filename"), u.url)))
                )
                .from(u)
                .where(u.question.survey.eq(survey)
                        .and(u.question.delFlag.eq(false))
                        .and(u.surveyPost.eq(surveyPost))
                        .and(u.question.answerType.in(AnswerType.FILE))
                        .and(u.answerType.in(AnswerType.FILE)))
                .fetch();

        return results.stream()
                .collect(Collectors.groupingBy(FileResultResponse::getQuestionId))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long questionId = entry.getKey();
                    List<FileResultResponse> fileResultResponses = entry.getValue();

                    // questionId에 대한 모든 answerType을 동일한 값으로 설정
                    AnswerType answerType = fileResultResponses.get(0).getQuestionType();

                    // 각 value(ChartResultResponse)의 answers를 합치기
                    List<FileInfo> combinedAnswers = fileResultResponses.stream()
                            .flatMap(fileResultResponse -> fileResultResponse.getFileInfos().stream())
                            .collect(Collectors.toList());

                    // 새로운 ChartResultResponse 객체 생성
                    return new FileResultResponse(questionId, answerType, combinedAnswers);
                })
                .collect(Collectors.toList());
    }
}

