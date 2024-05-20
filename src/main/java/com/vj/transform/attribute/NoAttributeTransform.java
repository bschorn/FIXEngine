package com.vj.transform.attribute;

import com.vj.model.attribute.StringAttribute;


public class NoAttributeTransform implements AttributeTransform<quickfix.Field,StringAttribute> {
    @Override
    public StringAttribute inbound(quickfix.Field value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public quickfix.Field outbound(StringAttribute value) {
        throw new UnsupportedOperationException();
    }
}
