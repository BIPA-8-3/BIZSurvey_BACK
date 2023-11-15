package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.request.*;
import com.bipa.bizsurvey.domain.survey.dto.response.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType;
import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
import com.bipa.bizsurvey.domain.survey.repository.AnswerRepository;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.LoginUser;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SurveyMapper surveyMapper;
    private final UserRepository userRepository;
    private final WorkspaceAdminRepository workspaceAdminRepository;


    public List<SurveyResponse> getSurveyList(Long workspaceId){
        List<Survey> surveyList = surveyRepository.findAllByWorkspaceId(workspaceId);
        List<SurveyResponse> result = new ArrayList<>();

        for(Survey survey : surveyList){
            SurveyResponse surveyResponse = SurveyResponse.builder()
                    .surveyId(survey.getId())
                    .title(survey.getTitle())
                    .build();
            result.add(surveyResponse);
        }
        return result;
    }

    public SurveyInWorkspaceResponse getSurvey(Long surveyId, LoginUser loginUser, Long workspaceId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        checkPermission(loginUser, workspaceId);
        // get survey
        SurveyInWorkspaceResponse surveyDto = surveyMapper.toSurveyInWorkspaceResponse(survey);
        // get question
        Long surveyKey = survey.getId();
        List<QuestionInWorkspaceResponse> questionDtoList = surveyMapper
                .toQuestionInWorkspaceResponseList(questionRepository.findAllBySurveyIdAndDelFlagFalse(surveyKey));

        // get answer
        questionDtoList.forEach(questionDto -> {
                Long questionKey = questionDto.getQuestionId();
                List<AnswerInWorkspaceResponse> answerDtoList = surveyMapper
                        .toAnswerInWorkspaceResponseList(answerRepository.findAllByQuestionIdAndDelFlagFalse(questionKey));
                questionDto.setAnswers(answerDtoList);
        });
        surveyDto.setQuestions(questionDtoList);
        return surveyDto;
    }

    public void createSurvey(CreateSurveyRequest createSurveyRequest, LoginUser loginUser, Long workspaceId){
        checkPermission(loginUser, workspaceId);
        Long surveyId = addSurvey(createSurveyRequest, loginUser, workspaceId);
        List<CreateQuestionRequest> questionRequests = createSurveyRequest.getQuestions();
        addQuestions(questionRequests, surveyId);
    }

    public void updateSurvey(UpdateSurveyRequest updateSurveyRequest, LoginUser loginUser, Long workspaceId){
        Survey survey = findSurvey(updateSurveyRequest.getSurveyId());
        checkAvailable(survey);
        checkPermission(loginUser, workspaceId);
        Long surveyId = modifySurvey(updateSurveyRequest, loginUser, workspaceId);
        deleteQuestionAndAnswer(surveyId);
        modifyQuestions(updateSurveyRequest.getUpdateQuestions());
        addQuestions(updateSurveyRequest.getCreateQuestions(), surveyId);
    }

    public void deleteSurvey(Long surveyId, LoginUser loginUser, Long workspaceId){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        checkPermission(loginUser, workspaceId);
        survey.setDelFlag(true);
        surveyRepository.save(survey);
    }


    // create survey & check permission
    private Long addSurvey(CreateSurveyRequest createSurveyRequest, LoginUser loginUser, Long workspaceId){
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        // save survey
        Survey survey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(survey);
        return survey.getId();
    }


    // update survey
    private Long modifySurvey(UpdateSurveyRequest updateSurveyRequest, LoginUser loginUser, Long workspaceId) {
        Survey survey = findSurvey(updateSurveyRequest.getSurveyId());
        survey.updateSurvey(updateSurveyRequest);
        surveyRepository.save(survey);
        return survey.getId();
    }


    // create questions
    private void addQuestions(List<CreateQuestionRequest> createQuestionRequest, Long surveyId){
        Survey survey = findSurvey(surveyId);
        createQuestionRequest.forEach(createQuestionDto -> {
            Question question = Question.toEntity(createQuestionDto, survey);
            questionRepository.save(question);
            addAnswer(createQuestionDto.getAnswers(), question);
        });
    }

    // update questions
    private void modifyQuestions(List<UpdateQuestionRequest> updateQuestionRequest){
        updateQuestionRequest.forEach(updateQuestionDto -> {
            Question question = questionRepository.findById(updateQuestionDto.getQuestionId()).orElseThrow();
            question.updateQuestion(updateQuestionDto);
            question.setDelFlag(false);
            questionRepository.save(question);
            addAnswer(updateQuestionDto.getAnswers(), question);
        });
    }

    // update 전처리 -> delete question, answer
    private void deleteQuestionAndAnswer(Long surveyId) {
        List<Question> questionList =questionRepository.findAllBySurveyId(surveyId);
        questionList.forEach(question -> {
            // delete question
            question.setDelFlag(true);
            // delete answers
            List<Answer> answerList = answerRepository.findAllByQuestionIdAndDelFlagFalse(question.getId());
            answerList.forEach(answer -> {
                answer.setDelFlag(true);
            });
        });
    }

    // create answers
    private void addAnswer(List<CreateAnswerRequest> createAnswerRequests, Question question){
        createAnswerRequests.forEach(answerDto -> {
            Answer answer = Answer.toEntity(answerDto, question);
            answerRepository.save(answer);
        });
    }


    private Survey findSurvey(Long surveyId){
        return surveyRepository.findById(surveyId).orElseThrow(
                () -> new SurveyException(SurveyExceptionType.NOT_EXIST_SURVEY)
        );
    }


    private void checkAvailable(Survey survey){
        if (survey.getDelFlag()){
            throw new SurveyException(SurveyExceptionType.ALREADY_DELETED);
        }
    }


    private void checkPermission(LoginUser loginUser, Long workspaceId){
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();

        if (workspace.getWorkspaceType().equals(WorkspaceType.COMPANY)) {
            if (workspaceAdminRepository.findByWorkspaceIdAndUserId(user.getId(), workspace.getId()) == null) {
                throw new SurveyException(SurveyExceptionType.NO_PERMISSION);
            }
        }else {
            if(workspaceRepository.findByUserIdAndWorkspaceType(user.getId(), WorkspaceType.PERSONAL) == null){
                throw new SurveyException(SurveyExceptionType.NO_PERMISSION);
            }
        }
    }



}
