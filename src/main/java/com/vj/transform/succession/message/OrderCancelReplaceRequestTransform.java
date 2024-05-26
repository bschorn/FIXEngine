package com.vj.transform.succession.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.succession.field.OrdTypeTransform;
import com.vj.transform.succession.field.SecurityIDSourceTransform;
import com.vj.transform.succession.field.SideTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.OrderCancelReplaceRequest;

public class OrderCancelReplaceRequestTransform implements MessageTransform<OrderCancelReplaceRequest, EquityOrder> {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;
    private final SecurityIDSourceTransform securityIDSourceTransform;

    public OrderCancelReplaceRequestTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordTypeTransform = transformers.field(OrdType.class);
        this.sideTransform = transformers.field(Side.class);
        this.securityIDSourceTransform = transformers.field(SecurityIDSource.class);
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(OrderCancelReplaceRequest message, SessionID sessionID, Object... objects) throws FieldNotFound {
        return services.orders().find(new ClientOrderId(message.getClOrdID().getValue()));
    }

    /**
     * Buy-Side
     */
    @Override
    public OrderCancelReplaceRequest outbound(EquityOrder equityOrder) throws NoTransformationException {
        OrderCancelReplaceRequest message = new OrderCancelReplaceRequest();
        // required
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
