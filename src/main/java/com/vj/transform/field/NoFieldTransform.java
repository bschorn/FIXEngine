package com.vj.transform.field;

import com.vj.model.attribute.StringAttribute;
import quickfix.Field;


public class NoFieldTransform implements FieldTransform<Field,StringAttribute> {
    @Override
    public StringAttribute inbound(quickfix.Field value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public quickfix.Field outbound(StringAttribute value) {
        throw new UnsupportedOperationException();
    }
}
