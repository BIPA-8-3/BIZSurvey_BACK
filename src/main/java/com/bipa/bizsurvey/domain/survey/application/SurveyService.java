package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.survey.*;
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
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType.NOT_EXIST_SURVEY;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SurveyMapper surveyMapper;
    private final UserRepository userRepository;

    public SurveyInWorkspaceResponse getSurvey(Long surveyId, LoginUser loginUser){

        // get survey
        SurveyInWorkspaceResponse surveyDto = surveyMapper
                .toSurveyInWorkspaceResponse(
                        checkPermission(
                        checkAvailable(
                                surveyRepository.findByIdAndDelFlagFalse(surveyId)), loginUser.getId()));

        // get question
        Long surveyKey = surveyDto.getSurveyId();
        List<QuestionInWorkspaceResponse> questionDtoList = surveyMapper
                .toQuestionInWorkspaceResponseList(questionRepository.findAllBySurveyId(surveyKey));

        // get answer
        questionDtoList.forEach(questionDto -> {
            Long questionKey = questionDto.getQuestionId();
            List<AnswerInWorkspaceResponse> answerDtoList = surveyMapper
                    .toAnswerInWorkspaceResponseList(answerRepository.findAllByQuestionId(questionKey));
            questionDto.setAnswers(answerDtoList);
        });

        surveyDto.setQuestions(questionDtoList);

        return surveyDto;
    }


    public void createSurvey(CreateSurveyRequest createSurveyRequest, LoginUser loginUser){

        // get workspace, user
        Workspace workspace = workspaceRepository.findByUserIdAndWorkspaceType(loginUser.getId(), WorkspaceType.PERSONAL);
        User user = userRepository.findById(loginUser.getId()).orElseThrow();

        // save survey
        Survey survey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(survey);

        // save question, answer
        List<CreateQuestionRequest> questionDtoList = createSurveyRequest.getQuestions();
        createQuestionAndAnswer(survey, questionDtoList);

    }


    public void updateSurvey(UpdateSurveyRequest updateSurveyRequest, LoginUser loginUser){

        // check survey
       Survey survey = checkPermission(
                        checkAvailable(
                                surveyRepository.findById(updateSurveyRequest.getSurveyId()).orElseThrow()
                        ), loginUser.getId());

        // update survey
        survey.updateSurvey(updateSurveyRequest);
        surveyRepository.save(survey);

        // question, answer delete
        questionRepository.deleteAllBySurveyId(survey.getId());

        // save question, answer
        List<CreateQuestionRequest> questionDtoList = updateSurveyRequest.getQuestions();
        createQuestionAndAnswer(survey, questionDtoList);
    }

    public void deleteSurvey(Long surveyId, LoginUser loginUser){

        Survey survey = checkPermission(surveyRepository.findById(surveyId).orElseThrow(),loginUser.getId());

        



    }






    private void createQuestionAndAnswer(Survey survey, List<CreateQuestionRequest> questionDtoList){
        questionDtoList.forEach(questionDto -> {
            //save question
            Question question = Question.toEntity(questionDto, survey);
            questionRepository.save(question);
            //save answer
            List<CreateAnswerRequest> answerDtoList = questionDto.getAnswers();
            answerDtoList.forEach(answerDto->{
                Answer answer = Answer.toEntity(answerDto, question);
                answerRepository.save(answer);
            });

        });
    }


    private Survey checkAvailable(Survey survey){
        if (survey.getDelFlag()){
            throw new SurveyException(SurveyExceptionType.ALREADY_DELETED);
        }
        return survey;
    }

    private Survey checkPermission(Survey survey, Long userId){
        if (!Objects.equals(survey.getUser().getId(), userId)) {
            throw new SurveyException(SurveyExceptionType.NO_PERMISSION);
        }
        return survey;
    }






}
