package com.vj.model.attribute;

public enum OrderType implements Attribute<Integer> {
    MARKET(1001),
    LIMIT(1002),
    STOP(1003),
    STOP_LIMIT(1004),
    LIMIT_CLOSE(1011);

    int value;
    OrderType(int value) {
        this.value = value;
    }

    @Override
    public Integer asValue() {
        return value;
    }
}
