package com.vj.handler.order.buyside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.BrokerOrderId;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Order;
import com.vj.transform.NoTransformationException;
import com.vj.transform.succession.field.OrdStatusTransform;
import com.vj.transform.succession.message.MessageTransform;
import com.vj.transform.succession.message.ExecutionReportTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.fix42.ExecutionReport;


public abstract class ExecutionReportHandler implements MessageHandler<ExecutionReport> {

    private static final Logger log = LoggerFactory.getLogger(ExecutionReportHandler.class);

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
            Order updatedOrder = equityOrder.update()
                    .orderState(newOrderState)
                    .brokerOrderId(new BrokerOrderId(executionReport.getOrderID().getValue()))
                    .end();
            // Update OrderService
            services().orders().update(updatedOrder);
        } catch (FieldNotFound fnf) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + fnf.getMessage());
            log.error(fnf.getMessage(), fnf);
        } catch (NoTransformationException nte) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + nte.getMessage());
            log.error(nte.getMessage(), nte);
        }
    }


    protected ExecutionReportTransform executionReportTransform() {
        return this.executionReportTransform;
    }

    protected OrdStatusTransform ordStatusTransform() {
        return this.executionReportTransform.orderStateTransform();
    }

}
