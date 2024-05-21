package com.vj.transform.entity;

import com.vj.model.attribute.InstrumentSource;
import com.vj.model.attribute.OrderType;
import com.vj.model.attribute.Side;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.attribute.InstrumentSourceTransform;
import com.vj.transform.attribute.OrderTypeTransform;
import com.vj.transform.attribute.SideTransform;
import com.vj.transform.attribute.TradeDateTransform;
import quickfix.SessionID;
import quickfix.field.ClOrdID;
import quickfix.field.TransactTime;
import quickfix.fix44.OrderCancelRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderCancelRequestTransform implements EntityTransform<OrderCancelRequest, EquityOrder> {

    private final Services services;
    private final OrderTypeTransform orderTypeTransform;
    private final SideTransform sideTransform;
    private final InstrumentSourceTransform instrumentSourceTransform;
    private final TradeDateTransform tradeDateTransform;

    public OrderCancelRequestTransform(Services services, Transformers transformers) {
        this.services = services;
        this.orderTypeTransform = transformers.attribute(OrderType.class);
        this.sideTransform = transformers.attribute(Side.class);
        this.instrumentSourceTransform = transformers.attribute(InstrumentSource.class);
        this.tradeDateTransform = transformers.attribute(LocalDate.class);
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(OrderCancelRequest newOrderSingle, SessionID sessionID, Object...objects) {
        throw new UnsupportedOperationException(OrderCancelRequestTransform.class.getSimpleName() + ".inbound(...)");
    }

    /**
     * Buy-Side
     */
    @Override
    public OrderCancelRequest outbound(EquityOrder equityOrder) {
        OrderCancelRequest message = new OrderCancelRequest();
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new TransactTime(LocalDateTime.now()));
        return message;
    }
}
