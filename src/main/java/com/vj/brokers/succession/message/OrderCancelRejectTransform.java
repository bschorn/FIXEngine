package com.vj.brokers.succession.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.OrderService;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.field.OrdStatusTransform;
import com.vj.transform.field.SideTransform;
import com.vj.transform.message.MessageTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.OrderCancelReject;

/**
 * Succession Transform
 */
public class OrderCancelRejectTransform implements MessageTransform<OrderCancelReject, EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelRejectTransform.class);

    private final Services services;
    private final SideTransform sideTransform;
    private final OrdStatusTransform ordStatusTransform;

    public OrderCancelRejectTransform(Services services, Transformers transformers) {
        this.services = services;
        this.sideTransform = transformers.field(Side.class);
        this.ordStatusTransform = transformers.field(OrdStatus.class);
    }

    @Override
    public Class<OrderCancelReject> messageClass() {
        return OrderCancelReject.class;
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(OrderCancelReject message, SessionID sessionID, Object...objects) throws FieldNotFound, OrderService.NoOrderFoundException {
        return services.orders().find(new ClientOrderId(message.getClOrdID().getValue()));
    }

    /**
     * Buy-Side
     */
    @Override
    public OrderCancelReject outbound(EquityOrder equityOrder) throws NoTransformationException {
        OrderCancelReject message = new OrderCancelReject();
        // required
        message.set(new Account(equityOrder.account().asValue()));
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new OrigClOrdID(equityOrder.origClientOrderId().asValue()));
        message.set(ordStatusTransform.outbound(equityOrder.orderState()));
        // optional
        //message.set(new Text());

        return message;
    }
}
