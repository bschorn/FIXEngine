package com.vj.handler.order;

import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.entity.EntityTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.ExecType;
import quickfix.fix44.ExecutionReport;


public class OrderPartialFillHandler extends ExecutionReportHandler {


    public OrderPartialFillHandler(EntityTransform entityTransform) {
        super(entityTransform);
    }

    @Override
    public boolean test(ExecutionReport executionReport, SessionID sessionID) {
        try {
            return (executionReport.getExecType().getValue() == ExecType.TRADE &&
                executionReport.getLeavesQty().getValue() > 0.0);
        } catch (FieldNotFound e) {
            Session.lookupSession(sessionID).getLog().onErrorEvent(" Caught exception: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void handle(ExecutionReport executionReport, SessionID sessionID) {
        try {
            // Retrieve the current version of the order for this executionReport
            EquityOrder equityOrder = equityStateTransform().inbound(executionReport, sessionID);
            // Transform the OrderStatus into OrderState
            OrderState newOrderState = equityStateTransform().orderStateTransform().inbound(executionReport.getOrdStatus());
            // Update OrderService
            services().orders().update(
                    // Create new version of order
                    equityOrder.update()
                            .totalFillQty(executionReport.getCumQty().getValue())
                            .avgFillPrice(executionReport.getAvgPx().getValue())
                            .orderState(newOrderState)
                            .end()
            );
        } catch (FieldNotFound fnf) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + fnf.getMessage());
        }
    }

}
