package com.vj.model.attribute;

public enum OrderState implements Attribute<Integer> {
    OPEN_REQ,
    OPEN_SENT,
    OPEN_ERR,
    OPEN_PEND, // PendingNew
    OPEN, // New
    REJECTED, // Rejected
    PARTIAL, // PartiallyFilled
    FILLED, // Filled
    CANCEL_REQ,
    CANCEL_SENT,
    CANCEL_PEND, // PendingCancel
    CANCELED, // Canceled
    REPLACE_REQ,
    REPLACE_SENT,
    REPLACE_PEND, // PendingReplace
    UNKNOWN;

    @Override
    public Integer asValue() {
        return ordinal();
    }

    @Override
    public String toString() {
        return name();
    }
}
