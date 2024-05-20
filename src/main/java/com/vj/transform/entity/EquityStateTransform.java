package com.vj.transform.entity;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.InstrumentSource;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.attribute.InstrumentSourceTransform;
import com.vj.transform.attribute.OrderStateTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix44.ExecutionReport;

public class EquityStateTransform implements EntityTransform<ExecutionReport,EquityOrder> {

    private final Services services;
    private final OrderStateTransform orderStateTransform;
    private final InstrumentSourceTransform instrumentSourceTransform;

    public EquityStateTransform(Services services, Transformers transformers) {
        this.services = services;
        this.orderStateTransform = transformers.attribute(OrderState.class);
        this.instrumentSourceTransform = transformers.attribute(InstrumentSource.class);
    }

    /**
     * BuySide
     */
    @Override
    public EquityOrder inbound(ExecutionReport executionReport, SessionID sessionID) throws FieldNotFound {
        return services.orders().find(new ClientOrderId(executionReport.getClOrdID().getValue()));
    }

    /**
     * SellSide
     */
    @Override
    public ExecutionReport outbound(EquityOrder equityOrder) {
        ExecutionReport message = new ExecutionReport();
        /*
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(new SecurityID(equityOrder.instrument().get(InstrumentSource.SEDOL)));
        message.set(instrumentSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(tradeDateTransform.outbound(services.markets().getTradeDate(Market.US_EQUITY)));
        message.set(new OrderQty(equityOrder.quantity()));
        message.set(new Price(equityOrder.price()));
        message.set(orderTypeTransform.outbound(equityOrder.orderType()));
        message.set(new TimeInForce(TimeInForce.DAY));
        message.set(new TransactTime(LocalDateTime.now()));
         */
        return message;
    }

    public OrderStateTransform orderStateTransform() {
        return orderStateTransform;
    }
}
