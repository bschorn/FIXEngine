package com.vj.model.attribute;

/**
 *
 */
public enum OrderType implements Attribute<Integer> {
    MARKET(1001), // <-- 1001 was made up, it should be whatever your system has for market order.
    LIMIT(1002), // <-- 1002 was made up, it should be whatever your system has for limit order.
    STOP(1003), // <-- 1003 was made up, it should be whatever your system has for stop order.
    STOP_LIMIT(1004), // <-- 1004 was made up, it should be whatever your system has for stop-limit order.
    PEGGED(1020); // <-- 1020 was made up, it should be whatever your system has for pegged order.

    int value;
    OrderType(int value) {
        this.value = value;
    }

    @Override
    public Integer asValue() {
        return value;
    }
}
