package com.bipa.bizsurvey.domain.community.exception.voteException;

import com.bipa.bizsurvey.global.exception.BaseException;
import com.bipa.bizsurvey.global.exception.BaseExceptionType;

public class VoteException extends BaseException {
    //

    private final BaseExceptionType baseExceptionType;

    public VoteException(BaseExceptionType baseExceptionType) {
        this.baseExceptionType = baseExceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return baseExceptionType;
    }
}
