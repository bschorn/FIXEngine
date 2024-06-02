package com.vj.transform.succession.field;

import com.vj.transform.NoTransformationException;

public interface FieldTransform<T extends quickfix.Field,R>  {

    Class<T> fieldClass();

    R inbound(T t) throws NoTransformationException;
    T outbound(R r) throws NoTransformationException;

}
