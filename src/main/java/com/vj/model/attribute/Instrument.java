package com.vj.model.attribute;

import java.util.HashMap;
import java.util.Map;

public class Instrument extends LongAttribute {

    private final String ticker;
    private Map<InstrumentSource,Object> alternateMap = null;
    public Instrument(long value, String ticker, Object...alternantives) {
        super(value);
        this.ticker = ticker;
        if (alternantives.length > 0) {
            alternateMap = new HashMap<>();
        }
        for (int i = 1; i < alternantives.length; i = i + 2) {
            if (alternantives[i-1] instanceof InstrumentSource) {
                alternateMap.put((InstrumentSource) alternantives[i-1], alternantives[i]);
            }
        }
    }

    @Override
    public String toString() {
        return ticker;
    }

    public boolean has(InstrumentSource instrumentSource) {
        return alternateMap.containsKey(instrumentSource);
    }

    public <T> T get(InstrumentSource instrumentSource) {
        return (T) alternateMap.get(instrumentSource);
    }
}
