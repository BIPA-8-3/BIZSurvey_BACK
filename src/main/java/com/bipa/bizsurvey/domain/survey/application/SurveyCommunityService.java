package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.community.repository.SurveyPostRepository;
import com.bipa.bizsurvey.domain.survey.domain.QUserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
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
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.global.common.storage.StorageService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SurveyCommunityService {

    //

    private final SurveyPostRepository surveyPostRepository;
    private final QuestionRepository questionRepository;
    private final UserSurveyResponseRepository userSurveyResponseRepository;
    private final SurveyService surveyService;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SurveyRepository surveyRepository;
    private final StorageService storageService;

    public QUserSurveyResponse u = new QUserSurveyResponse("u");


    public void participateSurvey(List<ParticipateSurveyRequest> participateSurvey, Long postId, LoginUser loginUser){
            User user = userRepository.findById(loginUser.getId()).orElseThrow();
            SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);
            List<String> url  = new ArrayList<>();
            for(ParticipateSurveyRequest survey : participateSurvey){
                Question question = questionRepository.findById(survey.getQuestionId()).orElseThrow();

                // 필수 체크
                Boolean b = question.getIsRequired();
                // 필수일 때
                if (b != null && b && survey.getAnswer() == null){
                    throw  new SurveyException(SurveyExceptionType.MISSING_REQUIRED_VALUE);
                }
                survey.getAnswer().forEach((answer) -> {
                    if(survey.getUrl() != null && !survey.getUrl().isEmpty()){
                        url.add(survey.getUrl());
                    }
                    UserSurveyResponse userSurveyResponse = UserSurveyResponse.toEntity(survey, user, question, surveyPost, answer);
                    userSurveyResponseRepository.save(userSurveyResponse);
                });
            }
            storageService.confirmStorageOfTemporaryFiles(url);

    }




    // 설문 페이지
    public SurveyResponse getSurvey(Long postId){
        SurveyPost surveyPost = surveyPostRepository.findByPostId(postId);

        return surveyService.getSurvey(surveyPost.getSurvey().getId());
    }

    // 참여 회원 체크
    public boolean checkUserParticipation(Long postId, LoginUser loginUser){
        Long count = jpaQueryFactory
                .select(u.count())
                .from(u)
                .where(u.surveyPost.post.id.eq(postId)
                        .and(u.user.id.eq(loginUser.getId()))
                )
                .fetchOne();

        if(count == 0){
            return false;
        }else{
            return true;
        }

    }



    // 설문 게시글 등록 시 본인 설문지 목록
    public List<SurveyListInCommunityResponse> getSurveyList(LoginUser loginUser){
        Long userId = loginUser.getId();
        String plan = loginUser.getPlan();

        List<Survey> list = surveyRepository.getSurveyList(userId);
        List<SurveyListInCommunityResponse> response = new ArrayList<>();

        for(Survey survey : list){
            response.add(
                    new SurveyListInCommunityResponse(
                            survey.getId(),
                            survey.getTitle(),
                            survey.getWorkspace().getWorkspaceName(),
                            survey.getWorkspace().getWorkspaceType(),
                            survey.getUser().getNickname()
                    )
            );
        }
        return response;

    }

    // 참가 인원수 조회
    public int getParticipants(Long surveyPostId){
        return userSurveyResponseRepository.findParticipants(surveyPostId);
    }


}
