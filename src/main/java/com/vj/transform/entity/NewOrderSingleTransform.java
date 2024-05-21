package com.vj.transform.entity;

import com.vj.model.attribute.*;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Market;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.attribute.InstrumentSourceTransform;
import com.vj.transform.attribute.OrderTypeTransform;
import com.vj.transform.attribute.SideTransform;
import com.vj.transform.attribute.TradeDateTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.ClOrdID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SecurityID;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NewOrderSingleTransform implements EntityTransform<NewOrderSingle,EquityOrder> {

    private final Services services;
    private final OrderTypeTransform orderTypeTransform;
    private final SideTransform sideTransform;
    private final InstrumentSourceTransform instrumentSourceTransform;
    private final TradeDateTransform tradeDateTransform;

    public NewOrderSingleTransform(Services services, Transformers transformers) {
        this.services = services;
        this.orderTypeTransform = transformers.attribute(OrderType.class);
        this.sideTransform = transformers.attribute(Side.class);
        this.instrumentSourceTransform = transformers.attribute(InstrumentSource.class);
        this.tradeDateTransform = transformers.attribute(LocalDate.class);
    }

    /**
     *  Sell-Side
     */
    @Override
    public EquityOrder inbound(NewOrderSingle newOrderSingle, SessionID sessionID, Object...objects) {
        try {
            // this is a kludge
            if (objects.length == 0 || !(objects[0] instanceof OrderId)) {
                throw new RuntimeException(this.getClass().getSimpleName() + ".inbound(...) requires third paramater to be an OrderId.");
            }
            OrderId orderId = (OrderId) objects[0];
            // end of kludge


            return EquityOrder.create(orderId, new Client(sessionID.getTargetCompID()))
                       .orderType(orderTypeTransform.inbound(newOrderSingle.getOrdType()))
                       .side(sideTransform.inbound(newOrderSingle.getSide()))
                       .instrument(services.products().find(instrumentSourceTransform.inbound(newOrderSingle.getSecurityIDSource()), newOrderSingle.getSecurityID().getValue()))
                       .orderQty(newOrderSingle.getOrderQty().getValue())
                       .limitPrice(newOrderSingle.getPrice().getValue())
                       .tradeDate(LocalDate.now()) // this will need to be replaced
                       .end();
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
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
        message.set(instrumentSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(tradeDateTransform.outbound(services.markets().getTradeDate(Market.US_EQUITY)));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(orderTypeTransform.outbound(equityOrder.orderType()));
        message.set(new TimeInForce(TimeInForce.DAY));
        message.set(new TransactTime(LocalDateTime.now()));
        return message;
    }
}
