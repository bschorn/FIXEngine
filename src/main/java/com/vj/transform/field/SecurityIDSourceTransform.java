package com.vj.transform.field;

import com.vj.model.attribute.InstrumentSource;
import quickfix.field.SecurityIDSource;

public class SecurityIDSourceTransform implements FieldTransform<SecurityIDSource,InstrumentSource> {
    @Override
    public InstrumentSource inbound(SecurityIDSource securityIDSource) {
        if (securityIDSource.getValue().equals("2")) {
            return InstrumentSource.SEDOL;
        }
        return InstrumentSource.RIC;
    }

    @Override
    public SecurityIDSource outbound(InstrumentSource instrumentSource) {
        if (instrumentSource == InstrumentSource.SEDOL) {
            return new SecurityIDSource(SecurityIDSource.SEDOL);
        }
        return new SecurityIDSource(SecurityIDSource.RIC_CODE);
    }
}
