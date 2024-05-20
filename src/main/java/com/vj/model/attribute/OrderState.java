package com.vj.model.attribute;

public enum OrderState implements Attribute<Integer> {
    CREATED,
    SENT,
    PENDING,
    OPEN,
    REJECTED,
    PARTIAL,
    FILLED,
    CANCEL_REQUEST,
    CANCEL_SENT,
    CANCEL_PENDING,
    CANCELED,
    CANCEL_REJECTED;

    @Override
    public Integer asValue() {
        return ordinal();
    }

    @Override
    public String toString() {
        return name();
    }
}
