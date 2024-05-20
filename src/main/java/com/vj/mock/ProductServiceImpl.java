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
        Map<String,Instrument> sedolMap = instrumentMap.get(InstrumentSource.SEDOL);
        sedolMap.put("93049503", new Instrument(5000, "MSFT", InstrumentSource.SEDOL, "93049503"));
    }

    public void register(Instrument instrument) {

    }


    @Override
    public Instrument find(InstrumentSource source, String value) {
        return instrumentMap.getOrDefault(source, Collections.emptyMap()).get(value);
    }
}
