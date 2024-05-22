package com.vj.transform.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.InstrumentSource;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.field.OrdTypeTransform;
import com.vj.transform.field.SecurityIDSourceTransform;
import com.vj.transform.field.SideTransform;
import com.vj.transform.field.TradeDateTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix44.OrderCancelReplaceRequest;

import java.time.LocalDateTime;

public class OrderCancelReplaceRequestTransform implements MessageTransform<OrderCancelReplaceRequest, EquityOrder> {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;
    private final SecurityIDSourceTransform securityIDSourceTransform;
    private final TradeDateTransform tradeDateTransform;

    public OrderCancelReplaceRequestTransform(Services services, Transformers transformers) {
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
    public EquityOrder inbound(OrderCancelReplaceRequest message, SessionID sessionID, Object... objects) throws FieldNotFound {
        return services.orders().find(new ClientOrderId(message.getClOrdID().getValue()));
    }

    /**
     * Buy-Side
     */
    @Override
    public OrderCancelReplaceRequest outbound(EquityOrder equityOrder) {
        OrderCancelReplaceRequest message = new OrderCancelReplaceRequest();
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new TransactTime(LocalDateTime.now()));
        message.set(new OrigClOrdID(equityOrder.origClientOrderId().asValue()));
        message.set(new OrderID(equityOrder.brokerOrderId().asValue()));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(securityIDSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(new SecurityID(equityOrder.instrument().get(InstrumentSource.SEDOL)));
        return message;
    }
}
