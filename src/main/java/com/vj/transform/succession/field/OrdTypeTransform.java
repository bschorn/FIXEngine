package com.vj.transform.succession.field;

import com.vj.model.attribute.OrderType;
import com.vj.transform.NoTransformationException;
import quickfix.field.OrdType;

import java.util.HashMap;
import java.util.Map;

public class OrdTypeTransform implements FieldTransform<OrdType,OrderType> {

    private final char[] outboundValues = { OrdType.MARKET, OrdType.LIMIT, OrdType.STOP, OrdType.STOP_LIMIT, OrdType.PEGGED };

    private final Map<Character,OrderType> mapInbound;
    private final Map<OrderType,OrdType> mapOutbound;

    public OrdTypeTransform() {
        mapInbound = new HashMap<>();
        mapOutbound = new HashMap<>();
        OrderType[] orderTypes = OrderType.values();
        for (int i = 0; i < orderTypes.length; i++) {
            mapInbound.put(outboundValues[i], orderTypes[i]);
            mapOutbound.put(orderTypes[i], new OrdType(outboundValues[i]));
        }
    }

    @Override
    public Class<OrdType> fieldClass() {
        return OrdType.class;
    }

    @Override
    public OrderType inbound(quickfix.field.OrdType ordType) throws NoTransformationException {
        OrderType orderType = mapInbound.get(ordType.getValue());
        if (orderType == null) {
            throw new NoTransformationException(OrderType.class, quickfix.field.OrdType.class, ordType.getValue());
        }
        return orderType;
    }

    @Override
    public quickfix.field.OrdType outbound(OrderType orderType) throws NoTransformationException {
        quickfix.field.OrdType ordType = mapOutbound.get(orderType);
        if (ordType == null) {
            throw new NoTransformationException(quickfix.field.OrdType.class, OrderType.class, orderType.toString());
        }
        return ordType;
    }
}
