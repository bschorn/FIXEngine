package com.vj.transform.message;

import com.vj.model.attribute.Client;
import com.vj.model.attribute.InstrumentSource;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Market;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.field.SecurityIDSourceTransform;
import com.vj.transform.field.OrdTypeTransform;
import com.vj.transform.field.SideTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import java.time.LocalDateTime;

public class NewOrderSingleTransform implements MessageTransform<NewOrderSingle,EquityOrder> {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;
    private final SecurityIDSourceTransform securityIDSourceTransform;

    public NewOrderSingleTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordTypeTransform = transformers.field(OrdType.class);
        this.sideTransform = transformers.field(Side.class);
        this.securityIDSourceTransform = transformers.field(SecurityIDSource.class);
    }

    /**
     *  Sell-Side
     */
    @Override
    public EquityOrder inbound(NewOrderSingle newOrderSingle, SessionID sessionID, Object...objects) throws FieldNotFound {
        // this is a kludge
        if (objects.length == 0 || !(objects[0] instanceof OrderId)) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".inbound(...) requires third paramater to be an OrderId.");
        }
        OrderId orderId = (OrderId) objects[0];
        // end of kludge

        Client client = services.clients().lookup(sessionID.getTargetCompID());
        return EquityOrder.create(orderId, client)
                   .orderType(ordTypeTransform.inbound(newOrderSingle.getOrdType()))
                   .side(sideTransform.inbound(newOrderSingle.getSide()))
                   .instrument(services.products().find(securityIDSourceTransform.inbound(newOrderSingle.getSecurityIDSource()), newOrderSingle.getSecurityID().getValue()))
                   .orderQty(newOrderSingle.getOrderQty().getValue())
                   .limitPrice(newOrderSingle.getPrice().getValue())
                   .tradeDate(services.markets().getTradeDate(Market.US_EQUITY)) // TODO
                   .end();
    }

    /**
     * Buy-Side
     */
    @Override
    public NewOrderSingle outbound(EquityOrder equityOrder) {
        NewOrderSingle message = new NewOrderSingle();
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(new SecurityID(equityOrder.instrument().get(InstrumentSource.SEDOL)));
        message.set(securityIDSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        message.set(new TimeInForce(TimeInForce.DAY));
        message.set(new TransactTime(LocalDateTime.now()));
        return message;
    }
}
