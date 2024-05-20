package com.vj.validator;

import quickfix.SessionID;

public interface MessageValidator<T extends quickfix.Message> {

    ValidatorResult apply(T message, SessionID sessionID);
}
