package com.bipa.bizsurvey.domain.survey.handler;


import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.domain.QUserSurveyResponse;
import com.bipa.bizsurvey.domain.survey.domain.Question;

import java.util.List;

public interface ResultTypeHandler {
    List<?> handleResponse(QUserSurveyResponse u, SurveyPost surveyPost, Question question);
}
