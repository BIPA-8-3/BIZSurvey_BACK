package com.bipa.bizsurvey.domain.survey.mapper;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.survey.AnswerInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.survey.QuestionInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.survey.SurveyInWorkspaceResponse;
import com.bipa.bizsurvey.domain.survey.dto.surveyresult.AnswerResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SurveyMapper {

    //get
    @Mapping(source = "id", target = "surveyId")
    SurveyInWorkspaceResponse toSurveyInWorkspaceResponse(Survey surveyEntity);

    @Mapping(source = "id", target = "questionId")
    QuestionInWorkspaceResponse toQuestionInWorkspaceResponse(Question questionEntity);
    List<QuestionInWorkspaceResponse> toQuestionInWorkspaceResponseList(List<Question> questionListEntity);

    @Mapping(source = "id", target = "answerId")
    AnswerInWorkspaceResponse toAnswerInWorkspaceResponse(Answer answer);
    List<AnswerInWorkspaceResponse> toAnswerInWorkspaceResponseList(List<Answer> answerListEntity);

    @Mappings({
            @Mapping(source = "question.id", target = "questionId"),
            @Mapping(source = "question.surveyQuestion", target = "surveyQuestion"),
            @Mapping(source = "question.answerType", target = "answerType"),
            @Mapping(source = "question.score", target = "score")
    })
    AnswerResponse toSurveyResultInPostResponse(UserSurveyResponse userSurveyResponse);
    List<AnswerResponse> toSurveyResultInPostResponseList(List<UserSurveyResponse> userSurveyResponses);


}
