package com.vj.model.attribute;

public enum OrderAction implements Attribute<Integer> {
    // BuySide Actions
    OPEN, // publish NewOrderSingle
    REPLACE, // publish OrderCancelReplace
    CANCEL, // publish OrderCancel
    WAIT, // publish nothing
    // SellSide Actions
    ACCEPT_OPEN, // publish ExecutionReport (ExecType.NEW)
    REJECT_OPEN, // publish ExecutionReport (ExecType.REJECTED)
    ACCEPT_REPLACE, // publish ExecutionReport (ExecType.REPLACE)
    REJECT_REPLACE, // publish ExecutionReport (ExecType.REJECTED)
    ACK_CANCEL, // publish ExecutionReprt (ExecType.PEND_CANCEL)
    ACCEPT_CANCEL, // publish ExecutionReport (ExecType.CANCELED)
    REJECT_CANCEL, // publish ExecutionReport (ExecType.REJECTED)
    TRADED, // publish ExecutionReport (ExecType.PARTIAL_FILL, ExecType.FILL)
    DONE_DAY, // publish ExecutionReport (ExecType.DONE_FOR_DAY)
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
