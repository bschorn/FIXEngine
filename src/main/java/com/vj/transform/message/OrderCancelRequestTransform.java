package com.vj.transform.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.field.SecurityIDSourceTransform;
import com.vj.transform.field.OrdTypeTransform;
import com.vj.transform.field.SideTransform;
import com.vj.transform.field.TradeDateTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix44.OrderCancelRequest;

import java.time.LocalDateTime;

public class OrderCancelRequestTransform implements MessageTransform<OrderCancelRequest, EquityOrder> {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;
    private final SecurityIDSourceTransform securityIDSourceTransform;
    private final TradeDateTransform tradeDateTransform;

    public OrderCancelRequestTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordTypeTransform = transformers.field(OrdType.class);
        this.sideTransform = transformers.field(Side.class);
        this.securityIDSourceTransform = transformers.field(SecurityIDSource.class);
        this.tradeDateTransform = transformers.field(TradeDate.class);
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(OrderCancelRequest message, SessionID sessionID, Object...objects) throws FieldNotFound {
        return services.orders().find(new ClientOrderId(message.getClOrdID().getValue()));
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
