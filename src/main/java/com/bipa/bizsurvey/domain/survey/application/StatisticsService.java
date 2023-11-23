package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.QSurveyPost;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.QAnswer;
import com.bipa.bizsurvey.domain.survey.domain.QUserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.response.*;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.enums.Correct;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final SurveyPostRepository surveyPostRepository;
    private final QuestionRepository questionRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    public QUserSurveyResponse u = new QUserSurveyResponse("u");
    public QSurveyPost sp = new QSurveyPost("sp");
    public QAnswer a = new QAnswer("a");

    //설문지 등록된 게시물 목록
    public List<SurveyPostListResponse> getSurveyPostList(Long surveyId){
        return jpaQueryFactory
                .select(Projections.constructor(SurveyPostListResponse.class, sp.post.id, sp.post.title))
                .from(sp)
                .innerJoin(sp.post)
                .where(sp.post.delFlag.eq(false)
                        .and(sp.survey.id.eq(surveyId)))
                .fetch();
    }

    //설문지 게시물 참여자 목록

    public List<ParticipantList> getSurveyUserList(Long surveyId, Long postId) {
        SurveyPost surveyPost = surveyPostRepository.findByPostIdAndSurveyId(postId, surveyId);

        return jpaQueryFactory
                .select(Projections.constructor(ParticipantList.class, u.user.id, u.user.nickname))
                .from(u)
                .innerJoin(u.user)
                .where(u.surveyPost.eq(surveyPost))
                .groupBy(u.user.id)
                .fetch();
    }

    // 개인 결과

    public List<PersonalResultResponse> getSurveyUserResult(Long surveyId, Long postId, String nickname) {

        SurveyPost surveyPost = surveyPostRepository.findByPostIdAndSurveyId(postId, surveyId);
        User user = userRepository.findByNickname(nickname).orElseThrow();

        return jpaQueryFactory
                .select(Projections.constructor(PersonalResultResponse.class,
                        u.question.id,
                        u.answer,
                        u.url,
                        u.question.answerType,
                        u.answerType
                        ))
                .from(u)
                .innerJoin(u.question)
                .where(u.user.id.eq(user.getId())
                        .and(u.surveyPost.id.eq(surveyPost.getId()))
                        .and(u.question.delFlag.eq(false)))
                .fetch();
    }

    //게시물 통계

    public StatisticsResponse getPostResult(Long postId) {
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyPost.getSurvey();

        List<ChartAndTextResponse> chartResults = processChartAndText(survey, surveyPost);
        List<FileResultResponse> fileResults = processFile(survey, surveyPost);

        return new StatisticsResponse(chartResults, fileResults);
    }



    // 점수형 설문 개별 통계

    public List<UserScoreResponse> getScoreUserAnswer(Long surveyId, Long postId, String nickname){
        SurveyPost surveyPost = surveyPostRepository.findByPostIdAndSurveyId(postId, surveyId);
        User user = userRepository.findByNickname(nickname).orElseThrow();

        List<UserScoreResponse> list = jpaQueryFactory
                .select(Projections.constructor(UserScoreResponse.class,
                        u.question.id,
                        u.answer,
                        a.correct,
                        u.question.score))
                .from(u)
                .innerJoin(a).on(u.question.id.eq(a.question.id))
                .innerJoin(u.question)
                .where(u.surveyPost.id.eq(surveyPost.getId())
                        .and(u.user.id.eq(user.getId()))
                        .and(u.answer.eq(a.surveyAnswer))
                        .and(a.delFlag.eq(false)))
                .fetch();


        return list;
    }

    // 점수형 설문 정답
    public List<ScoreAnswerResponse> getScoreAnswer(Long surveyId){

        return jpaQueryFactory
                .select(Projections.constructor(ScoreAnswerResponse.class,
                        a.question.id,
                        a.surveyAnswer))
                .from(a)
                .innerJoin(a.question)
                .where(a.question.delFlag.eq(false)
                        .and(a.question.survey.id.eq(surveyId))
                        .and(a.correct.eq(Correct.YES))
                        .and(a.delFlag.eq(false)))
                .fetch();
    }

    // 점수형 게시물 통계
    public List<ScoreResultResponse> getScoreResult(Long surveyId, Long postId){

        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyPost.getSurvey();

        List<ScoreResultResponse> list = jpaQueryFactory
                .select(Projections.constructor(ScoreResultResponse.class,
                        a.question.id,
                        Projections.list(Projections.constructor(ScoreAnswerCount.class,
                                a.surveyAnswer,
                                u.answer.count()))
                        ))
                .from(a)
                .leftJoin(u).on(a.question.eq(u.question).and(u.surveyPost.eq(surveyPost)).and(u.answer.eq(a.surveyAnswer)))
                .where(a.delFlag.eq(false)
                        .and(a.question.survey.eq(survey)))
                .groupBy(a.surveyAnswer, a.question)
                .fetch();

        // 그룹화된 데이터를 questionId를 기준으로 다시 그룹화
        Map<Long, List<ScoreResultResponse>> groupedResults = list.stream()
                .collect(Collectors.groupingBy(ScoreResultResponse::getQuestionId));

        // 각 그룹의 ScoreResultResponse를 처리하여 새로운 리스트 생성
        List<ScoreResultResponse> processedResults = groupedResults.entrySet().stream()
                .map(entry -> {
                    long questionId = entry.getKey();
                    List<ScoreAnswerCount> mergedAnswers = entry.getValue().stream()
                            .flatMap(result -> result.getAnswers().stream())
                            .collect(Collectors.toList());
                    return new ScoreResultResponse(questionId, mergedAnswers);
                })
                .collect(Collectors.toList());

        return processedResults;

    }




    public List<ChartAndTextResponse> processChartAndText(Survey survey, SurveyPost surveyPost){

        List<ChartAndTextResponse> results = jpaQueryFactory
                .select(Projections.constructor(ChartAndTextResponse.class, u.question.id, u.question.answerType.as("questionType"),
                        Projections.list(Projections.constructor(ChartAndTextResult.class, u.answer, u.count().as("count"))))
                )
                .from(u)
                .innerJoin(u.question)
                .where(u.question.survey.eq(survey)
                        .and(u.question.delFlag.eq(false))
                        .and(u.surveyPost.eq(surveyPost))
                        .and(u.question.answerType.notIn(AnswerType.FILE))
                        .and(u.answerType.notIn(AnswerType.FILE)))
                .groupBy(u.question, u.answer)
                .orderBy(u.question.step.asc())
                .fetch();

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
                .innerJoin(u.question)
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
