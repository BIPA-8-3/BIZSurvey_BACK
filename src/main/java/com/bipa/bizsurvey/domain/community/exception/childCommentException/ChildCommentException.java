package com.bipa.bizsurvey.domain.community.exception.childCommentException;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;

public class ChildCommentException extends BaseException {
    //

    private final BaseExceptionType baseExceptionType;

    public ChildCommentException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
