package com.bipa.bizsurvey.domain.survey.mapper;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.dto.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.SurveyInWorkspaceResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SurveyMapper {



    @Mapping(source = "id", target = "surveyId")
    SurveyInWorkspaceResponse toSurveyInWorkspaceResponse(Survey surveyEntity);

    @Mapping(source = "id", target = "questionId")
    QuestionInWorkspaceResponse toQuestionInWorkspaceResponse(Question questionEntity);
    List<QuestionInWorkspaceResponse> toQuestionInWorkspaceResponse(List<Question> questionListEntity);

    @Mapping(source = "id", target = "answerId")
    AnswerInWorkspaceResponse toAnswerInWorkspaceResponse(Answer answer);
    List<AnswerInWorkspaceResponse> toAnswerInWorkspaceResponse(List<Answer> answerListEntity);
}
