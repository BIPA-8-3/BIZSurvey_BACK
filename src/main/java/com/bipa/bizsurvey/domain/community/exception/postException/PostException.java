package com.bipa.bizsurvey.domain.community.exception.postException;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;


public class PostException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public PostException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
