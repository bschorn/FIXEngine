package com.vj.transform.message;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.OrderService;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.field.OrdStatusTransform;
import com.vj.transform.field.OrdTypeTransform;
import com.vj.transform.field.SideTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;

import java.time.LocalDateTime;
import java.util.UUID;

public class ExecutionReportTransform implements MessageTransform<ExecutionReport,EquityOrder> {

    private final Services services;
    private final OrdStatusTransform ordStatusTransform;
    //private final SecurityIDSourceTransform securityIDSourceTransform;
    private final SideTransform sideTransform;
    private final OrdTypeTransform ordTypeTransform;

    public ExecutionReportTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordStatusTransform = transformers.field(OrdStatus.class);
        //this.securityIDSourceTransform = transformers.field(SecurityIDSource.class);
        this.sideTransform = transformers.field(Side.class);
        this.ordTypeTransform = transformers.field(OrdType.class);
    }

    @Override
    public Class<ExecutionReport> messageClass() {
        return ExecutionReport.class;
    }

    /**
     * BuySide
     */
    @Override
    public EquityOrder inbound(ExecutionReport executionReport, SessionID sessionID, Object...objects) throws FieldNotFound, OrderService.NoOrderFoundException {
        EquityOrder currentOrder = services.orders().find(new ClientOrderId(executionReport.getClOrdID().getValue()));
        return currentOrder;
    }

    /**
     * SellSide
     */
    @Override
    public ExecutionReport outbound(EquityOrder equityOrder) throws NoTransformationException {
        ExecutionReport message = new ExecutionReport();
        message.set(new Account(equityOrder.account().asValue()));
        //message.set(new ExecType(ExecType.NEW));
        message.set(ordStatusTransform.outbound(equityOrder.orderState()));
        message.set(new OrderID(equityOrder.id().toString()));
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new SecondaryClOrdID("unset"));
        message.set(new ExecID(UUID.randomUUID().toString()));
        message.set(new ExecBroker(equityOrder.broker().asValue()));
        message.set(new ExecTransType('0')); // '0'-Normal
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new Rule80A(Rule80A.Day));
        //message.set(new SecurityID(equityOrder.instrument().get(InstrumentSource.SEDOL)));
        //message.set(securityIDSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(new LastQty(equityOrder.latestFillQty()));
        message.set(new LastPx(equityOrder.latestFillPrice()));
        message.set(new LeavesQty(equityOrder.unfilledQty()));
        message.set(new CumQty(equityOrder.totFillQty()));
        message.set(new AvgPx(equityOrder.avgFillPrice()));
        message.set(new TransactTime(LocalDateTime.now()));
        return message;
    }

    public OrdStatusTransform orderStateTransform() {
        return ordStatusTransform;
    }
}
