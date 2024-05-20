package com.vj.validator;

import java.util.Optional;

public interface ValidatorResult<T> {

    enum ResultType {
        OK, ERROR;
    }

    ResultType resultType();
    Optional<T> result();
}
