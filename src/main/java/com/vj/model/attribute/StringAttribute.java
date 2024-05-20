package com.vj.model.attribute;

public abstract class StringAttribute implements Attribute<String>, Comparable<StringAttribute> {

    private final String value;
    protected StringAttribute(String value) {
        this.value = value;
    }

    @Override
    public String asValue() {
        return value;
    }

    @Override
    public int compareTo(StringAttribute o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
