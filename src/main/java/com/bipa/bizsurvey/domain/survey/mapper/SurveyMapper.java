package com.bipa.bizsurvey.domain.survey.mapper;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.*;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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
    SurveyResultInPostResponse toSurveyResultInPostResponse(UserSurveyResponse userSurveyResponse);
    List<SurveyResultInPostResponse> toSurveyResultInPostResponseList(List<UserSurveyResponse> userSurveyResponses);


}
