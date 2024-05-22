package com.vj.model.attribute;

public class OrderId extends LongAttribute {
    public OrderId(long value) {
        super(value);
    }
    public OrderId(String value) {
        super(Long.valueOf(value));
    }
}
