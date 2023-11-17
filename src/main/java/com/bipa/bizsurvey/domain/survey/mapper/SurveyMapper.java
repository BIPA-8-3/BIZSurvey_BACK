package com.bipa.bizsurvey.domain.survey.mapper;

import com.bipa.bizsurvey.domain.survey.domain.Answer;
import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.domain.Survey;
import com.bipa.bizsurvey.domain.survey.domain.UserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.AnswerResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.ChartResultResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.QuestionResponse;
import com.bipa.bizsurvey.domain.survey.dto.response.SurveyResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SurveyMapper {

    //get
    @Mapping(source = "id", target = "surveyId")
    SurveyResponse toSurveyInWorkspaceResponse(Survey surveyEntity);

    @Mapping(source = "id", target = "questionId")
    QuestionResponse toQuestionInWorkspaceResponse(Question questionEntity);
    List<QuestionResponse> toQuestionInWorkspaceResponseList(List<Question> questionListEntity);

    @Mapping(source = "id", target = "answerId")
    AnswerResponse toAnswerInWorkspaceResponse(Answer answer);
    List<AnswerResponse> toAnswerInWorkspaceResponseList(List<Answer> answerListEntity);

//    @Mappings({
//            @Mapping(source = "question.id", target = "questionId"),
//            @Mapping(source = "question.surveyQuestion", target = "surveyQuestion"),
//            @Mapping(source = "question.answerType", target = "answerType"),
//            @Mapping(source = "question.score", target = "score")
//    })
//    ChartResultResponse toSurveyResultInPostResponse(UserSurveyResponse userSurveyResponse);
//    List<ChartResultResponse> toSurveyResultInPostResponseList(List<UserSurveyResponse> userSurveyResponses);
//

}
