package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.SurveyResultInPostResponse;
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

    public List<SurveyResultInPostResponse> getSurveyResultInPost(Long postId){

        SurveyPost surveyPost = surveyPostRepository.findById(1L).orElseThrow();

        // postId로 찾기
        List<SurveyResultInPostResponse> surveyResultInPostResponses = surveyMapper
                .toSurveyResultInPostResponseList(userSurveyResponseRepository.findAllByPostId(surveyPost));

        return surveyResultInPostResponses;
    }




}
