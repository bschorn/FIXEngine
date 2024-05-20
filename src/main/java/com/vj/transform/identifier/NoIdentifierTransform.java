package com.vj.transform.identifier;

import com.vj.model.attribute.StringAttribute;
import quickfix.Field;

public class NoIdentifierTransform implements IdentifierTransform<Field,StringAttribute> {
    @Override
    public StringAttribute inbound(Field value, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Field outbound(StringAttribute value, Object... objects) {
        throw new UnsupportedOperationException();
    }

}
