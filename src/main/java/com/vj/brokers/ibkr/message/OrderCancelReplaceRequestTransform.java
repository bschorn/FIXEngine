package com.vj.brokers.ibkr.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.OrderService;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.field.OrdTypeTransform;
import com.vj.transform.field.SideTransform;
import com.vj.transform.message.MessageTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.OrderCancelReplaceRequest;

public class OrderCancelReplaceRequestTransform implements MessageTransform<OrderCancelReplaceRequest, EquityOrder> {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;

    public OrderCancelReplaceRequestTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordTypeTransform = transformers.field(OrdType.class);
        this.sideTransform = transformers.field(Side.class);
    }

    @Override
    public Class<OrderCancelReplaceRequest> messageClass() {
        return OrderCancelReplaceRequest.class;
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(OrderCancelReplaceRequest message, SessionID sessionID, Object... objects) throws FieldNotFound, OrderService.NoOrderFoundException {
        return services.orders().find(new ClientOrderId(message.getOrigClOrdID().getValue()));
    }

    /**
     * Buy-Side
     */
    @Override
    public OrderCancelReplaceRequest outbound(EquityOrder equityOrder) throws NoTransformationException {
        OrderCancelReplaceRequest message = new OrderCancelReplaceRequest();
        // required
        message.set(new Account(equityOrder.account().asValue()));
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new OrigClOrdID(equityOrder.origClientOrderId().asValue()));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new Symbol(equityOrder.instrument().toString()));
        // optional
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        //message.set(new Account(equityOrder.account().asValue()));
        //message.set(new OrderID(equityOrder.brokerOrderId().asValue()));
        return message;
    }
}
