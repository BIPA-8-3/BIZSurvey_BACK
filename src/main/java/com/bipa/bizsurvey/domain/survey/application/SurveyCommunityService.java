package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.request.ParticipateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyListInCommunityResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.survey.repository.UserSurveyResponseRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SurveyCommunityService {

    private final SurveyPostRepository surveyPostRepository;
    private final QuestionRepository questionRepository;
    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyService surveyService;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SurveyRepository surveyRepository;


    public void participateSurvey(List<ParticipateSurveyRequest> participateSurvey, Long postId, LoginUser loginUser){
            User user = userRepository.findById(loginUser.getId()).orElseThrow();
            SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);

            for(ParticipateSurveyRequest survey : participateSurvey){
                Question question = questionRepository.findById(survey.getQuestionId()).orElseThrow();

                // 필수 체크
                Boolean b = question.getIsRequired();
                // 필수일 때
                if (b != null && b && survey.getAnswer() == null){
                    throw  new SurveyException(SurveyExceptionType.MISSING_REQUIRED_VALUE);
                }
                survey.getAnswer().forEach((answer) -> {
                    UserSurveyResponse userSurveyResponse = UserSurveyResponse.toEntity(survey, user, question, surveyPost, answer);
                    userSurveyResponseRepository.save(userSurveyResponse);
                });


            }

            addCount(postId);
    }

    private void addCount(Long postId){
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        surveyPost.addCount();
    }


    // 설문 페이지
    public SurveyResponse getSurvey(Long postId){
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);

        return surveyService.getSurvey(surveyPost.getSurvey().getId());
    }

    // 참여 회원 체크
    public boolean checkUserParticipation(Long postId, LoginUser loginUser){
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
        Long userId = loginUser.getId();
        boolean isExists = userSurveyResponseRepository.existsBySurveyPostIdAndUserId(surveyPost.getId(), userId);
        return isExists;
    }



    // 설문 게시글 등록 시 본인 설문지 목록
    public List<SurveyListInCommunityResponse> getSurveyList(LoginUser loginUser){
        Long userId = loginUser.getId();
        List<Object[]> list = surveyRepository.getSurveyList(userId);
        return list.stream()
                .map(result -> new SurveyListInCommunityResponse(
                        ((Number) result[0]).longValue(),
                        (String) result[1],
                        result[2] != null ? (String) result[2] : "",
                        WorkspaceType.valueOf((String) result[3]),
                        (String) result[4]
                ))
                .collect(Collectors.toList());

    }



}
