package com.vj.model.attribute;

import com.vj.model.entity.Market;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Broker implements Attribute<String> {
    DEFAULT(Market.US_EQUITY, "DEFAULT"),
    VCTV(Market.US_EQUITY, "VCTV");

    Market market;
    String value;
    Broker(Market market, String value) {
        this.market = market;
        this.value = value;
    }

    static final Map<String,Broker> MAP = Arrays.stream(Broker.values()).collect(Collectors.toMap(Broker::asValue, Function.identity()));

    @Override
    public String asValue() {
        return value;
    }

    public static Broker from(String value) {
        return MAP.get(value);
    }
}
