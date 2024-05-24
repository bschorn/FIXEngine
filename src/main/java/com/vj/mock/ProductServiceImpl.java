package com.vj.mock;

import com.vj.model.attribute.Instrument;
import com.vj.model.attribute.InstrumentSource;
import com.vj.service.ProductService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProductServiceImpl implements ProductService {

    private final Map<InstrumentSource,Map<String,Instrument>> instrumentMap = new HashMap<>();

    public ProductServiceImpl() {
        instrumentMap.put(InstrumentSource.SEDOL,new HashMap<>());
        instrumentMap.put(InstrumentSource.RIC,new HashMap<>());


        // fake instruments
        register(new Instrument(5000, "MSFT", InstrumentSource.SEDOL, "93049503", InstrumentSource.RIC, "MSFT"));
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
