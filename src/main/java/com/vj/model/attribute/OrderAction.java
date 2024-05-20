package com.vj.model.attribute;

public enum OrderAction implements Attribute<Integer> {
    OPEN,
    REPLACE,
    CANCEL,
    FILL,
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
