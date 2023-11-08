package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.dto.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyException;
import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
import com.bipa.bizsurvey.domain.survey.repository.AnswerRepository;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bipa.bizsurvey.domain.survey.exception.surveyException.SurveyExceptionType.NOT_EXIST_SURVEY;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SurveyMapper surveyMapper;

    public SurveyInWorkspaceResponse getSurvey(Long surveyId){

        // get survey
        SurveyInWorkspaceResponse surveyDto = surveyMapper
                .toSurveyInWorkspaceResponse(surveyRepository.findById(surveyId).orElseThrow(
                        () -> new SurveyException(NOT_EXIST_SURVEY))
                );


        // get question
        Long surveyKey = surveyDto.getId();
        List<QuestionInWorkspaceResponse> questionListDto = surveyMapper
                .toQuestionInWorkspaceResponse(questionRepository.findAllBySurveyId(surveyKey));

        // get answer
        questionListDto.forEach(question -> {
            Long questionKey = question.getId();
            List<AnswerInWorkspaceResponse> answerListDto = surveyMapper
                    .toAnswerInWorkspaceResponse(answerRepository.findAllByQuestionId(questionKey));
            question.setAnswers(answerListDto);
        });

        surveyDto.setQuestions(questionListDto);

        return surveyDto;
    }


    public void createSurvey(){



    }


}
