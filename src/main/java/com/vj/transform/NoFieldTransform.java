package com.vj.transform;

import com.vj.model.attribute.StringAttribute;
import com.vj.transform.field.FieldTransform;
import quickfix.Field;


public class NoFieldTransform implements FieldTransform<Field,StringAttribute> {
    @Override
    public Class<Field> fieldClass() {
        return null;
    }

    @Override
    public StringAttribute inbound(quickfix.Field value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public quickfix.Field outbound(StringAttribute value) {
        throw new UnsupportedOperationException();
    }
}
