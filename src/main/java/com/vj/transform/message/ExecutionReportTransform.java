package com.vj.transform.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.InstrumentSource;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.field.*;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;

import java.time.LocalDateTime;

public class ExecutionReportTransform implements MessageTransform<ExecutionReport,EquityOrder> {

    private final Services services;
    private final OrdStatusTransform ordStatusTransform;
    private final SecurityIDSourceTransform securityIDSourceTransform;
    private final SideTransform sideTransform;
    private final OrdTypeTransform ordTypeTransform;

    public ExecutionReportTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordStatusTransform = transformers.field(OrdStatus.class);
        this.securityIDSourceTransform = transformers.field(SecurityIDSource.class);
        this.sideTransform = transformers.field(Side.class);
        this.ordTypeTransform = transformers.field(OrdType.class);
    }

    /**
     * BuySide
     */
    @Override
    public EquityOrder inbound(ExecutionReport executionReport, SessionID sessionID, Object...objects) throws FieldNotFound {
        return services.orders().find(new ClientOrderId(executionReport.getClOrdID().getValue()));
    }

    /**
     * SellSide
     */
    @Override
    public ExecutionReport outbound(EquityOrder equityOrder) {
        ExecutionReport message = new ExecutionReport();
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(new SecurityID(equityOrder.instrument().get(InstrumentSource.SEDOL)));
        message.set(securityIDSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        message.set(new TimeInForce(TimeInForce.DAY));
        message.set(new TransactTime(LocalDateTime.now()));
        message.set(ordStatusTransform.outbound(equityOrder.orderState()));
        return message;
    }

    public OrdStatusTransform orderStateTransform() {
        return ordStatusTransform;
    }
}
