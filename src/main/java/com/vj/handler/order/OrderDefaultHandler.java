package com.vj.handler.order;

import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Order;
import com.vj.transform.entity.EntityTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.fix44.ExecutionReport;


public class OrderDefaultHandler extends ExecutionReportHandler {


    public OrderDefaultHandler(EntityTransform entityTransform) {
        super(entityTransform);
    }

    @Override
    public boolean isDefaultHandler() {
        return true;
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
