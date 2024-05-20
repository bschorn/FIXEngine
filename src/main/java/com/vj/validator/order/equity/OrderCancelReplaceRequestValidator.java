package com.vj.validator.order.equity;

import com.vj.validator.MessageValidator;
import com.vj.validator.ValidatorResult;
import quickfix.SessionID;
import quickfix.fix44.OrderCancelReplaceRequest;

import java.util.Optional;

public class OrderCancelReplaceRequestValidator implements MessageValidator<OrderCancelReplaceRequest> {
    @Override
    public ValidatorResult apply(OrderCancelReplaceRequest message, SessionID sessionID) {
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
