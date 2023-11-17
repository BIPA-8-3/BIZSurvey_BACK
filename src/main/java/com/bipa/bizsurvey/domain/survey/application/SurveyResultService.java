package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.QUserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.response.ChartResult;
import com.bipa.bizsurvey.domain.survey.dto.response.ChartResultResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.PersonalResultResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.TextResultResponse;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SurveyResultService {

    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyMapper surveyMapper;
    private final SurveyPostRepository surveyPostRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public void getSurveyResultOfPost(Long postId, String type){

        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Survey survey = surveyPost.getSurvey();
        List<Question> questions = questionRepository.findAllBySurveyIdAndDelFlagFalse(survey.getId());

        // 차트 결과
        List<ChartResultResponse> results = new ArrayList<>();
        // 주관식, 날짜
        List<TextResultResponse> answers = new ArrayList<>();

        questions.forEach(question -> {
            QUserSurveyResponse u = new QUserSurveyResponse("u");
            switch (type) {
                case "chart" :

                    List<ChartResult> responses = jpaQueryFactory
                            .select(Projections.constructor(ChartResult.class, u.answer, u.count().as("count")))
                            .from(u)
                            .where(u.surveyPost.eq(surveyPost))
                            .where(u.question.eq(question))
                            .where(u.answerType.notIn(AnswerType.FILE))
                            .groupBy(u.answer)
                            .fetch();

                    ChartResultResponse chart = new ChartResultResponse(question.getId(), responses);
                    results.add(chart);
                    return;
                case "text" :
                    List<String> responses2 = jpaQueryFactory
                            .select(u.answer)
                            .from(u)
                            .where(u.surveyPost.eq(surveyPost))
                            .where(u.question.eq(question))
                            .where(u.answerType.notIn(AnswerType.FILE))
                            .fetch();

                case "file" :

                    List<String> responses3 = jpaQueryFactory
                            .select(u.answer)
                            .from(u)
                            .where(u.surveyPost.eq(surveyPost))
                            .where(u.question.eq(question))
                            .where(u.answerType.in(AnswerType.FILE))
                            .fetch();

            }
        });
    }
















    public PersonalResultResponse getPersonalResultInPost(Long surveyPostId, String nickname){

        Long userId = userRepository.findByNickname(nickname).orElseThrow().getId();

        userSurveyResponseRepository.findBySurveyPostIdAndUserId(surveyPostId, userId);

        return null;

    }




}
