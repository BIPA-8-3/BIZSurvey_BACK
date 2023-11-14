package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.survey.*;
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
import com.bipa.bizsurvey.domain.workspace.domain.WorkspaceAdmin;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
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
    private final WorkspaceAdminRepository workspaceAdminRepository;

    public SurveyInWorkspaceResponse getSurvey(Long surveyId, LoginUser loginUser){

        // get survey
        SurveyInWorkspaceResponse surveyDto = surveyMapper
                .toSurveyInWorkspaceResponse(
                        checkPermission(
                        checkAvailable(
                                surveyRepository.findById(surveyId).orElseThrow()), loginUser.getId()));

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

        // question이 dto에 있으면 수정, 없으면 delflag변경
        List<UpdateQuestionRequest> questionDtoList = updateSurveyRequest.getQuestions();
        questionDtoList.forEach(questionDto -> {
            if (questionDto.getQuestionId() == null){
                //새로 생성
                Question question = Question.builder()
                        .surveyQuestion(questionDto.getSurveyQuestion())
                        .answerType(questionDto.getAnswerType())
                        .score(questionDto.getScore())
                        .step(questionDto.getStep())
                        .build();

                questionRepository.save(question);

                List<UpdateAnswerRequest> answerDtoList = questionDto.getAnswers();
                answerDtoList.forEach(answerDto ->{
                    Answer answer = Answer.builder()
                            .surveyAnswer(answerDto.getSurveyAnswer())
                            .correct(answerDto.getCorrect())
                            .question(question)
                            .build();
                    answerRepository.save(answer);
                });

            }else {
                // 수정
                Question question = questionRepository.findById(questionDto.getQuestionId()).orElseThrow();
                question.updateQuestion(questionDto);
                questionRepository.save(question);

                AnswerType answerType = questionDto.getAnswerType();
                if(answerType.equals(AnswerType.SINGLE_CHOICE) || answerType.equals(AnswerType.MULTIPLE_CHOICE)){
                    List<UpdateAnswerRequest> answerDtoList = questionDto.getAnswers();
                    answerRepository.deleteAllByQuestionId(question.getId());
                    answerDtoList.forEach(answerDto -> {
                        Answer answer = Answer.builder()
                                .surveyAnswer(answerDto.getSurveyAnswer())
                                .correct(answerDto.getCorrect())
                                .step(answerDto.getStep())
                                .question(question)
                                .build();
                        answerRepository.save(answer);
                    });
                }
            }
        });


    }

    public void deleteSurvey(Long surveyId, LoginUser loginUser){

        Survey survey = checkPermission(surveyRepository.findById(surveyId).orElseThrow(),loginUser.getId());
        survey.setDelFlag(true);

    }






    private void createQuestionAndAnswer(Survey survey, List<CreateQuestionRequest> questionDtoList){
        questionDtoList.forEach(questionDto -> {
            //save question
            Question question = Question.toEntity(questionDto, survey);
            questionRepository.save(question);
            //save answer
            if (question.getAnswerType().equals(AnswerType.SINGLE_CHOICE) || question.getAnswerType().equals(AnswerType.MULTIPLE_CHOICE)) {
                List<CreateAnswerRequest> answerDtoList = questionDto.getAnswers();
                answerDtoList.forEach(answerDto -> {
                    Answer answer = Answer.toEntity(answerDto, question);
                    answerRepository.save(answer);
                });
            }
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
