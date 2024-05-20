package com.vj.handler.order;

import com.vj.transform.entity.EntityTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.fix44.ExecutionReport;
import quickfix.field.ExecType;

/**
 * Broker acknowledges receiving order but hasn't placed into market.
 *
 */
public class OrderPendingHandler extends ExecutionReportHandler {


    public OrderPendingHandler(EntityTransform entityTransform) {
        super(entityTransform);
    }

    @Override
    public boolean test(ExecutionReport executionReport, SessionID sessionID) {
        try {
            return executionReport.getExecType().getValue() == ExecType.PENDING_NEW;
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
