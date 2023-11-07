package com.bipa.bizsurvey.domain.survey.application;

import com.bipa.bizsurvey.domain.survey.dto.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.mapper.SurveyMapper;
import com.bipa.bizsurvey.domain.survey.repository.AnswerRepository;
import com.bipa.bizsurvey.domain.survey.repository.QuestionRepository;
import com.bipa.bizsurvey.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final SurveyMapper surveyMapper;

    public SurveyInWorkspaceResponse getSurvey(Long surveyId){

        // get survey
        SurveyInWorkspaceResponse surveyDto = surveyMapper
                .toSurveyInWorkspaceResponse(surveyRepository.findById(surveyId).orElseThrow());

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


}
