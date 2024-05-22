package com.vj.transform.field;

import com.vj.model.attribute.Side;

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
    public Side inbound(quickfix.field.Side side) {
        return mapInbound.get(side.getValue());
    }

    @Override
    public quickfix.field.Side outbound(Side side) {
        return mapOutbound.get(side);
    }
}
