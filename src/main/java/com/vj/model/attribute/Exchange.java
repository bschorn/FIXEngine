package com.vj.model.attribute;

import com.vj.model.entity.Market;

public enum Exchange implements Attribute<String> {
    NYSE(Market.US_EQUITY, "NYX"),
    NASDAQ(Market.US_EQUITY, "NSDQ"),
    ANY_US_EQUITY(Market.US_EQUITY, "USEQ");

    Market market;
    String value;
    Exchange(Market market, String value) {
        this.market = market;
        this.value = value;
    }

    @Override
    public String asValue() {
        return null;
    }
}
