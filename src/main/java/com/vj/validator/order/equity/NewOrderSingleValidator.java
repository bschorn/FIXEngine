package com.vj.validator.order.equity;

import com.vj.validator.MessageValidator;
import com.vj.validator.ValidatorResult;
import quickfix.SessionID;
import quickfix.fix42.NewOrderSingle;

import java.util.Optional;

public class NewOrderSingleValidator implements MessageValidator<NewOrderSingle> {
    @Override
    public ValidatorResult apply(NewOrderSingle message, SessionID sessionID) {
        return new ValidatorResult() {

            @Override
            public ResultType resultType() {
                return ResultType.OK;
            }

            @Override
            public Optional result() {
                return Optional.empty();
            }
        };
    }
}
