package com.vj.model.attribute;

public abstract class LongAttribute implements Attribute<Long>, Comparable<LongAttribute> {

    private final long value;
    protected LongAttribute(long value) {
        this.value = value;
    }

    @Override
    public Long asValue() {
        return value;
    }

    @Override
    public int compareTo(LongAttribute o) {
        long a = this.value - o.value;
        if (a == 0) return 0;
        return (a < 0) ? -1 : 1;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) value;
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LongAttribute) {
            return value == ((LongAttribute) other).value;
        }
        return false;
    }
}
