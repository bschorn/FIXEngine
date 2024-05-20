package com.vj.handler.order;

import com.vj.transform.entity.EntityTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.ExecType;
import quickfix.fix44.ExecutionReport;


public class OrderRejectedHandler extends ExecutionReportHandler {


    public OrderRejectedHandler(EntityTransform entityTransform) {
        super(entityTransform);
    }

    @Override
    public boolean test(ExecutionReport executionReport, SessionID sessionID) {
        try {
            return executionReport.getExecType().getValue() == ExecType.REJECTED;
        } catch (FieldNotFound e) {
            Session.lookupSession(sessionID).getLog().onErrorEvent(" Caught exception: " + e.getMessage());
        }
        return false;
    }

    /*

    // Customize only if needed

    @Override
    public void handle(ExecutionReport executionReport, SessionID sessionID) {
        try {
            // Retrieve the current version of the order for this executionReport
            EquityOrder equityOrder = equityStateTransform().inbound(executionReport, sessionID);
            // Transform the OrderStatus into OrderState
            OrderState newOrderState = equityStateTransform().orderStateTransform().inbound(executionReport.getOrdStatus());
            // create new version of order
            Order updatedOrder = equityOrder.update(newOrderState);
            // Update OrderService
            services().orders().update(updatedOrder);
        } catch (FieldNotFound fnf) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + fnf.getMessage());
        }
    }
     */

}
