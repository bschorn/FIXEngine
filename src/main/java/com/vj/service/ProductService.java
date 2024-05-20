package com.vj.service;

import com.vj.model.attribute.Instrument;
import com.vj.model.attribute.InstrumentSource;

public interface ProductService {

    Instrument find(InstrumentSource source, String value);
}
