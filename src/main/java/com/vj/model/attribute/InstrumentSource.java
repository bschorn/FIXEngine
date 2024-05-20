package com.vj.model.attribute;

public enum InstrumentSource implements Attribute<String> {
    SEDOL, RIC;

    @Override
    public String asValue() {
        return name();
    }
}
