package com.vj.model.attribute;

public enum OrderAction implements Attribute<Integer> {
    OPEN,
    ACCEPT_OPEN,
    REJECT_OPEN,
    REPLACE,
    ACK_REPLACE,
    ACCEPT_REPLACE,
    REJECT_REPLACE,
    CANCEL,
    ACK_CANCEL,
    ACCEPT_CANCEL,
    REJECT_CANCEL,
    TRADED,
    NONE;

    @Override
    public Integer asValue() {
        return ordinal();
    }

    @Override
    public String toString() {
        return name();
    }
}
