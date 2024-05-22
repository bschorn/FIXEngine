package com.vj.transform.field;

public interface FieldTransform<T extends quickfix.Field,R>  {
    R inbound(T t);
    T outbound(R r);

}
