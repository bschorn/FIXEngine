package com.vj.handler.order.buyside;

import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.message.MessageTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.ExecType;
import quickfix.fix42.ExecutionReport;


public class OrderTradeHandler extends ExecutionReportHandler {


    public OrderTradeHandler(MessageTransform messageTransform) {
        super(messageTransform);
    }

    @Override
    public boolean test(ExecutionReport executionReport, SessionID sessionID) {
        try {
            switch (executionReport.getExecType().getValue()) {
                case ExecType.PARTIAL_FILL:
                case ExecType.FILL:
                    return true;
            }
        } catch (FieldNotFound e) {
            Session.lookupSession(sessionID).getLog().onErrorEvent(" Caught exception: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void handle(ExecutionReport executionReport, SessionID sessionID) {
        try {
            // Retrieve the current version of the order for this executionReport
            EquityOrder equityOrder = executionReportTransform().inbound(executionReport, sessionID);
            // Transform the OrderStatus into OrderState
            OrderState newOrderState = executionReportTransform().orderStateTransform().inbound(executionReport.getOrdStatus());
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
