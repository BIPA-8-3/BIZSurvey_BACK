package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.*;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
import com.bipa.bizsurvey.domain.survey.repository.AnswerRepository;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

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

    public SurveyInWorkspaceResponse getSurvey(Long surveyId){

        // get survey
        SurveyInWorkspaceResponse surveyDto = surveyMapper
                .toSurveyInWorkspaceResponse(surveyRepository.findById(surveyId).orElseThrow(
                        () -> new SurveyException(NOT_EXIST_SURVEY))
                );

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


    public void createSurvey(CreateSurveyRequest createSurveyRequest){

        // get workspace, user
        // 사용자 정보 받아오는 부분 수정하기
        Workspace workspace = workspaceRepository.findById(1L).orElseThrow();
        User user = User.builder().id(1L).build();

        // save survey
        Survey survey = Survey.toEntity(user, workspace, createSurveyRequest);
        surveyRepository.save(survey);

        // save question, answer
        List<CreateQuestionRequest> questionDtoList = createSurveyRequest.getQuestions();
        questionDtoList.forEach(questionDto ->{

            Question question = Question.toEntity(questionDto, survey);
            questionRepository.save(question);

            List<CreateAnswerRequest> answerDtoList = questionDto.getAnswers();
            answerDtoList.forEach(answerDto -> {
                Answer answer = Answer.toEntity(answerDto, question);
                answerRepository.save(answer);
            });

        });


    }


}
