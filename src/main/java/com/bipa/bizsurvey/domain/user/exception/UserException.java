package com.bipa.bizsurvey.domain.user.exception;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;


public class UserException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public UserException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
