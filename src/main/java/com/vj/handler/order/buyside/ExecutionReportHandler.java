package com.vj.handler.order.buyside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Order;
import com.vj.transform.field.OrdStatusTransform;
import com.vj.transform.message.MessageTransform;
import com.vj.transform.message.ExecutionReportTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.fix42.ExecutionReport;


public abstract class ExecutionReportHandler implements MessageHandler<ExecutionReport> {

    private final ExecutionReportTransform executionReportTransform;

    public ExecutionReportHandler(MessageTransform messageTransform) {
        if (messageTransform instanceof ExecutionReportTransform) {
            this.executionReportTransform = (ExecutionReportTransform) messageTransform;
        } else {
            throw new UnsupportedOperationException(ExecutionReportHandler.class.getSimpleName() +
                    " constructor is expecting " + ExecutionReportTransform.class.getSimpleName());
        }
    }

    @Override
    public String msgType() {
        return ExecutionReport.MSGTYPE;
    }

    @Override
    public void handle(ExecutionReport executionReport, SessionID sessionID) {
        try {
            // Retrieve the current version of the order for this executionReport
            EquityOrder equityOrder = executionReportTransform().inbound(executionReport, sessionID);
            // Transform the OrderStatus into OrderState
            OrderState newOrderState = executionReportTransform().orderStateTransform().inbound(executionReport.getOrdStatus());
            // create new version of order
            Order updatedOrder = equityOrder.update(newOrderState);
            // Update OrderService
            services().orders().update(updatedOrder);
        } catch (FieldNotFound fnf) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + fnf.getMessage());
        }
    }


    protected ExecutionReportTransform executionReportTransform() {
        return this.executionReportTransform;
    }

    protected OrdStatusTransform ordStatusTransform() {
        return this.executionReportTransform.orderStateTransform();
    }

}
