package com.bipa.bizsurvey.domain.survey.exception.surveyException;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;

public class SurveyException extends BaseException {

    //

    private final BaseExceptionType baseExceptionType;

    public SurveyException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
