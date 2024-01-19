package com.bipa.bizsurvey.domain.workspace.exception;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;

public class EncryptionException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public EncryptionException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
