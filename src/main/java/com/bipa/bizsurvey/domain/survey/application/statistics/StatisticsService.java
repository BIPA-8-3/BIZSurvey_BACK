package com.bipa.bizsurvey.domain.survey.application.statistics;

import com.bipa.bizsurvey.domain.survey.dto.response.StatisticsResponse;

public interface StatisticsService {

    StatisticsResponse getPostResult(Long surveyId, Long postId, String type);

}
