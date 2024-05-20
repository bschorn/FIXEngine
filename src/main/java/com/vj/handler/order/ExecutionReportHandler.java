package com.vj.handler.order;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Order;
import com.vj.transform.attribute.OrderStateTransform;
import com.vj.transform.entity.EntityTransform;
import com.vj.transform.entity.EquityStateTransform;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.fix44.ExecutionReport;


public abstract class ExecutionReportHandler implements MessageHandler<ExecutionReport> {

    private final EquityStateTransform equityStateTransform;

    public ExecutionReportHandler(EntityTransform entityTransform) {
        if (entityTransform instanceof EquityStateTransform) {
            this.equityStateTransform = (EquityStateTransform) entityTransform;
        } else {
            throw new UnsupportedOperationException(ExecutionReportHandler.class.getSimpleName() +
                    " constructor is expecting " + EquityStateTransform.class.getSimpleName());
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


    protected EquityStateTransform equityStateTransform() {
        return this.equityStateTransform;
    }

    protected OrderStateTransform orderStateTransform() {
        return this.equityStateTransform.orderStateTransform();
    }

}
