package com.vj.transform.identifier;

public interface IdentifierTransform<T,R> {

    R inbound(T t, Object...objects);
    T outbound(R r, Object...objects);
}
