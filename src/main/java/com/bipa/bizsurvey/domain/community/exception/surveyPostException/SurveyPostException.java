package com.bipa.bizsurvey.domain.community.exception.surveyPostException;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;

public class SurveyPostException extends BaseException {
    private final BaseExceptionType baseExceptionType;

    public SurveyPostException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
