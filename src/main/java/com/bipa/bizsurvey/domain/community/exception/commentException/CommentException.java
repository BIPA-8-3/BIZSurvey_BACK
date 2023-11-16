package com.bipa.bizsurvey.domain.community.exception.commentException;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;

public class CommentException extends BaseException {

    private final BaseExceptionType baseExceptionType;

    public CommentException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
