package com.vj.transform.succession.field;

import com.vj.model.attribute.Side;
import com.vj.transform.NoTransformationException;

import java.util.HashMap;
import java.util.Map;

public class SideTransform implements FieldTransform<quickfix.field.Side,Side> {

    private final char[] outboundValues = {quickfix.field.Side.BUY, quickfix.field.Side.SELL, quickfix.field.Side.SELL_SHORT};

    private final Map<Character,Side> mapInbound;
    private final Map<Side,quickfix.field.Side> mapOutbound;

    public SideTransform() {
        mapInbound = new HashMap<>();
        mapOutbound = new HashMap<>();
        Side[] sides = Side.values();
        for (int i = 0; i < sides.length; i++) {
            mapInbound.put(outboundValues[i], sides[i]);
            mapOutbound.put(sides[i], new quickfix.field.Side(outboundValues[i]));
        }
    }

    @Override
    public Side inbound(quickfix.field.Side qfSide) throws NoTransformationException {
        Side side = mapInbound.get(qfSide.getValue());
        if (qfSide == null) {
            throw new NoTransformationException(Side.class, quickfix.field.Side.class, qfSide.toString());
        }
        return side;
    }

    @Override
    public quickfix.field.Side outbound(Side side) throws NoTransformationException {
        quickfix.field.Side qfSide = mapOutbound.get(side);
        if (qfSide == null) {
            throw new NoTransformationException(quickfix.field.Side.class, Side.class, side.toString());
        }
        return qfSide;
    }
}
