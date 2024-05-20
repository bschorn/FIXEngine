package com.vj.model.attribute;

public class OrderVersion extends LongAttribute {
    private OrderVersion(long value) {
        super(value);
    }
    public static OrderVersion initial() {
        return new OrderVersion(0);
    }
    public OrderVersion getNext() {
        return new OrderVersion(this.asValue()+1);
    }
}
