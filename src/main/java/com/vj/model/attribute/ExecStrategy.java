package com.vj.model.attribute;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 *
 */
public enum ExecStrategy implements Attribute<String> {
    DEFAULT(Broker.DEFAULT, "DEFAULT"),
    VLCT(Broker.VCTV, "VLCT");

    Broker broker;
    String value;
    ExecStrategy(Broker broker, String value) {
        this.broker = broker;
        this.value = value;
    }

    static final Map<String, ExecStrategy> MAP = Arrays.stream(ExecStrategy.values()).collect(Collectors.toMap(ExecStrategy::asValue, Function.identity()));

    @Override
    public String asValue() {
        return value;
    }

    public Broker broker() { return broker; }

    public static ExecStrategy from(String value) {
        return MAP.get(value);
    }
}
