package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.request.*;
import com.bipa.bizsurvey.domain.survey.dto.response.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public SurveyInWorkspaceResponse getSurvey(Long surveyId, LoginUser loginUser){
        // get survey
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        SurveyInWorkspaceResponse surveyDto = surveyMapper.toSurveyInWorkspaceResponse(survey);

        // get question
        Long surveyKey = surveyDto.getSurveyId();
        List<QuestionInWorkspaceResponse> questionDtoList = surveyMapper
                .toQuestionInWorkspaceResponseList(questionRepository.findAllBySurveyIdAndDelFlagFalse(surveyKey));

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


    public void createSurvey(CreateSurveyRequest createSurveyRequest, LoginUser loginUser, Long workspaceId){

        // get workspace, user
        User user = userRepository.findById(loginUser.getId()).orElseThrow();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        // 로그인 유저가 해당 워크스페이스에 설문지를 등록할 수 있는 권한이 있는가?
        checkCreatePermission(user, workspace);

        // save survey
        Survey survey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(survey);

        // save question, answer
        List<CreateQuestionRequest> questionDtoList = createSurveyRequest.getQuestions();
        questionDtoList.forEach(questionDto -> {
            //save question
            Question question = createQuestion(questionDto, survey);
            //save answer
            List<CreateAnswerRequest> answerDtoList = questionDto.getAnswers();
            answerDtoList.forEach(answerDto -> {
                createAnswer(answerDto, question);
            });
        });
    }


    public void updateSurvey(UpdateSurveyRequest updateSurveyRequest, LoginUser loginUser){
        // check survey
        Survey survey = findSurvey(updateSurveyRequest.getSurveyId());
        checkPermission(survey, loginUser.getId());
        // update survey
        survey.updateSurvey(updateSurveyRequest);
        surveyRepository.save(survey);

        List<UpdateQuestionRequest> updateQuestionRequests = updateSurveyRequest.getUpdateQuestions();

        // update question
        updateQuestionRequests.forEach(dto -> {
            Question question = questionRepository.findById(dto.getQuestionId()).orElseThrow();
            AnswerType answerType = question.getAnswerType();

            // delete answers
            if (answerType.equals(AnswerType.SINGLE_CHOICE) || answerType.equals(AnswerType.MULTIPLE_CHOICE)){
                List<Answer> answers = answerRepository.findAllByQuestionIdAndDelFlagFalse(question.getId());
                answers.forEach(answer -> {
                    answer.setDelFlag(true);
                });
            }
            // create answers
            dto.getAnswers().forEach(answerDto -> {createAnswer(answerDto, question);});
            question.updateQuestion(dto);
            questionRepository.save(question);
         }
        );


        // delete question
        Set<Long> updateQuestionIds = updateQuestionRequests.stream()
                .map(UpdateQuestionRequest::getQuestionId)
                .collect(Collectors.toSet());

        List<Question> questions = questionRepository.findAllBySurveyId(survey.getId());
        questions.forEach(question -> {
            if (!updateQuestionIds.contains(question.getId())){
                question.setDelFlag(true);
                questionRepository.save(question);
            }
        });

        // create question
        updateSurveyRequest.getCreateQuestions().forEach(dto -> {
            Question question = Question.toEntity(dto, survey);
            questionRepository.save(question);

            //create answers
            dto.getAnswers().forEach(answerDto -> {
                createAnswer(answerDto, question);
            });
        });

    }


    // create questions
    private void createQuestion(List<CreateQuestionRequest> createQuestionRequests, Survey survey){
        createQuestionRequests.forEach(questionDto -> {
            Question question = Question.toEntity(questionDto, survey);
            questionRepository.save(question);
        });
    }



    // update questions
    private void updateQuestion(List<UpdateQuestionRequest> updateQuestionRequests){
        updateQuestionRequests.forEach(questionDto -> {
            Question question = questionRepository.findById(questionDto.getQuestionId()).orElseThrow();
            question.updateQuestion(questionDto);
            questionRepository.save(question);
        });
    }

    // delete question
    private void deleteQuestion(){

    }

    // create answers
    private void createAnswer(List<CreateAnswerRequest> createAnswerRequests, Question question){
        createAnswerRequests.forEach(answerDto -> {
            Answer answer = Answer.toEntity(answerDto, question);
            answerRepository.save(answer);
        });
    }


    // delete answers
    private void deleteAnswer(){

    }












    public void deleteSurvey(Long surveyId, LoginUser loginUser){
        Survey survey = findSurvey(surveyId);
        checkAvailable(survey);
        checkPermission(survey, loginUser.getId());
        survey.setDelFlag(true);
    }





    private Question createQuestion(CreateQuestionRequest questionDto, Survey survey){
            Question question = Question.toEntity(questionDto, survey);
            questionRepository.save(question);
        return question;
    }

    private void createAnswer(CreateAnswerRequest createAnswerRequest, Question question){

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

    private void checkPermission(Survey survey, Long userId){
        if (!Objects.equals(survey.getUser().getId(), userId)) {
            throw new SurveyException(SurveyExceptionType.NO_PERMISSION);
        }
    }


    private void checkCreatePermission(User user, Workspace workspace){
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
