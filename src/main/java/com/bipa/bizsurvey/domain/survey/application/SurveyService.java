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
        List<QuestionInWorkspaceResponse> questionListDto = surveyMapper
                .toQuestionInWorkspaceResponseList(questionRepository.findAllBySurveyId(surveyKey));

        // get answer
        questionListDto.forEach(question -> {
            Long questionKey = question.getQuestionId();
            List<AnswerInWorkspaceResponse> answerListDto = surveyMapper
                    .toAnswerInWorkspaceResponseList(answerRepository.findAllByQuestionId(questionKey));
            question.setAnswers(answerListDto);
        });

        surveyDto.setQuestions(questionListDto);

        return surveyDto;
    }


    public int createSurvey(CreateSurveyRequest createSurveyRequest){

        //워크스페이스, 유저 아이디 필요
        Optional<Workspace> workspace = workspaceRepository.findById(1L);


        //설문 가져오기











        return 1;

    }


}
