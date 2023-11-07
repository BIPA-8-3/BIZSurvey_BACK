package com.bipa.bizsurvey.domain.survey.mapper;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.SurveyInWorkspaceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SurveyMapper {

    SurveyInWorkspaceResponse toSurveyInWorkspaceResponse(Survey surveyEntity);
    List<QuestionInWorkspaceResponse> toQuestionInWorkspaceResponse(List<Question> questionListEntity);
    List<AnswerInWorkspaceResponse> toAnswerInWorkspaceResponse(List<Answer> answerListEntity);
}
