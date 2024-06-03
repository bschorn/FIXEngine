package com.vj.mock;

import com.vj.model.attribute.Instrument;
import com.vj.model.attribute.InstrumentSource;
import com.vj.reference.NasdaqListed;
import com.vj.service.ProductService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * TODO:
 *
 * Add ability to read symbols from -> https://www.nasdaqtrader.com/dynamic/SymDir/nasdaqlisted.txt
 */
public class ProductServiceImpl implements ProductService {

    private final Map<InstrumentSource,Map<String,Instrument>> instrumentMap = new HashMap<>();

    public ProductServiceImpl() {
        instrumentMap.put(InstrumentSource.NASDAQ,new HashMap<>());

        AtomicLong instrumentId = new AtomicLong(1000);
        try {
            NasdaqListed nasdaqListed = new NasdaqListed();
            nasdaqListed.getSymbols().stream()
                    .forEach(sym -> register(new Instrument(instrumentId.incrementAndGet(), sym, InstrumentSource.NASDAQ, sym)));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void register(Instrument instrument) {
        for (InstrumentSource instrumentSource : instrumentMap.keySet()) {
            if (instrument.has(instrumentSource)) {
                instrumentMap.get(instrumentSource).put(instrument.get(instrumentSource), instrument);
            }
        }
    }


    @Override
    public Instrument find(InstrumentSource source, String value) {
        return instrumentMap.getOrDefault(source, Collections.emptyMap()).get(value);
    }
}
