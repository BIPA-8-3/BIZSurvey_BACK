package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.AnswerResponse;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.SurveyResultResponse;
import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SurveyResultService {

    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyMapper surveyMapper;
    private final SurveyPostRepository surveyPostRepository;

    public SurveyResultResponse getSurveyResultInPost(Long postId){

        // postId로 surveyPost찾아오기
//        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId).orElseThrow();
        SurveyPost surveyPost = surveyPostRepository.findById(postId).orElseThrow();

        //참여자들
        List<String> users = userSurveyResponseRepository.findDistinctNicknamesBySurveyPostId(surveyPost);

        // 결과들
        List<AnswerResponse> answerResponses = surveyMapper
                .toSurveyResultInPostResponseList(userSurveyResponseRepository.findAllByPostId(surveyPost));

        SurveyResultResponse surveyResultResponse = new SurveyResultResponse(users, answerResponses);

        return surveyResultResponse;
    }




}
