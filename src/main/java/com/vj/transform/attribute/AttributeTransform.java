package com.vj.transform.attribute;

public interface AttributeTransform<T extends quickfix.Field,R>  {
    R inbound(T t);
    T outbound(R r);

}
