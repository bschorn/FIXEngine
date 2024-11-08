package com.vj.model.attribute;

public enum Side implements Attribute<Integer> {
    B(101),
    S(102),
    SS(105);

    int value;
    Side(int value) {
        this.value = value;
    }

    @Override
    public Integer asValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }

    public static Side from(String value) {
        for (Side side : Side.values()) {
            if (side.name().equalsIgnoreCase(value)) {
                return side;
            }
        }
        throw new IllegalArgumentException("Unknown side value: " + value);
    }
}
