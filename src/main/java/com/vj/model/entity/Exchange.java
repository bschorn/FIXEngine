package com.vj.model.entity;

public enum Exchange {
    NYSE(Market.US_EQUITY), NASDAQ(Market.US_EQUITY);

    Market market;
    Exchange(Market market) {
        this.market = market;
    }
}
