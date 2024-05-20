package com.vj.transform.attribute;

import com.vj.model.attribute.OrderType;
import quickfix.field.OrdType;

import java.util.HashMap;
import java.util.Map;

public class OrderTypeTransform implements AttributeTransform<quickfix.field.OrdType,OrderType> {

    private final char[] outboundValues = {OrdType.MARKET, OrdType.LIMIT, OrdType.STOP_STOP_LOSS, OrdType.STOP_LIMIT, OrdType.LIMIT_ON_CLOSE};

    private final Map<Character,OrderType> mapInbound;
    private final Map<OrderType,OrdType> mapOutbound;

    public OrderTypeTransform() {
        mapInbound = new HashMap<>();
        mapOutbound = new HashMap<>();
        OrderType[] orderTypes = OrderType.values();
        for (int i = 0; i < orderTypes.length; i++) {
            mapInbound.put(outboundValues[i], orderTypes[i]);
            mapOutbound.put(orderTypes[i], new OrdType(outboundValues[i]));
        }
    }

    @Override
    public OrderType inbound(quickfix.field.OrdType ordType) {
        return mapInbound.get(ordType.getValue());
    }

    @Override
    public quickfix.field.OrdType outbound(OrderType orderType) {
        return mapOutbound.get(orderType);
    }
}
