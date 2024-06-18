package com.vj.brokers.ibkr.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.OrderService;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.field.SideTransform;
import com.vj.transform.message.MessageTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.OrderCancelRequest;

/**
 * Succession Transform
 */
public class OrderCancelRequestTransform implements MessageTransform<OrderCancelRequest, EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelRequestTransform.class);

    private final Services services;
    private final SideTransform sideTransform;

    public OrderCancelRequestTransform(Services services, Transformers transformers) {
        this.services = services;
        this.sideTransform = transformers.field(Side.class);
    }

    @Override
    public Class<OrderCancelRequest> messageClass() {
        return OrderCancelRequest.class;
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(OrderCancelRequest message, SessionID sessionID, Object...objects) throws FieldNotFound, OrderService.NoOrderFoundException {
        return services.orders().find(new ClientOrderId(message.getOrigClOrdID().getValue()));
    }

    /**
     * Buy-Side
     */
    @Override
    public OrderCancelRequest outbound(EquityOrder equityOrder) throws NoTransformationException {
        OrderCancelRequest message = new OrderCancelRequest();
        // required
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new OrigClOrdID(equityOrder.origClientOrderId().asValue()));
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(sideTransform.outbound(equityOrder.side()));
        // optional
        //message.set(new Account(equityOrder.account().asValue()));

        return message;
    }
}
