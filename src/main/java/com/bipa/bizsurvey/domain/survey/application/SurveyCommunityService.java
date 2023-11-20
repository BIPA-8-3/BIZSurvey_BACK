package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.request.ParticipateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class SurveyCommunityService {

    private final SurveyPostRepository surveyPostRepository;
    private final QuestionRepository questionRepository;
    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyService surveyService;

    // TODO : 사용자 정보 받아오기
    public void participateSurvey(List<ParticipateSurveyRequest> participateSurvey, Long postId){
            User user = User.builder().id(3L).build();
            SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);

            for(ParticipateSurveyRequest survey : participateSurvey){
                Question question = questionRepository.findById(survey.getQuestionId()).orElseThrow();

                // 필수 체크
                Boolean b = question.getIsRequired();
                // 필수일 때
                if (b != null && b && survey.getAnswer() == null){
                    throw  new SurveyException(SurveyExceptionType.MISSING_REQUIRED_VALUE);
                }

                UserSurveyResponse userSurveyResponse = UserSurveyResponse.toEntity(survey, user, question, surveyPost);
                userSurveyResponseRepository.save(userSurveyResponse);
            }
    }


    public SurveyResponse getSurvey(Long postId){
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        SurveyResponse surveyResponse = surveyService.getSurvey(surveyPost.getSurvey().getId());
        return surveyResponse;
    }




}
