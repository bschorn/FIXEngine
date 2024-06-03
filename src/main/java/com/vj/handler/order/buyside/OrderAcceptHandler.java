package com.vj.handler.order.buyside;

import com.vj.model.attribute.BrokerOrderId;
import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.succession.message.MessageTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.ExecType;
import quickfix.fix42.ExecutionReport;


public class OrderAcceptHandler extends ExecutionReportHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderAcceptHandler.class);

    public OrderAcceptHandler(MessageTransform messageTransform) {
        super(messageTransform);
    }

    @Override
    public boolean isHandler(ExecutionReport executionReport, SessionID sessionID) {
        try {
            switch (executionReport.getExecType().getValue()) {
                case ExecType.NEW:
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
                            .orderState(newOrderState)
                            .orderAction(OrderAction.NONE)
                            .brokerOrderId(new BrokerOrderId(executionReport.getExecID().getValue()))
                            .end()
            );
        } catch (FieldNotFound fnf) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + fnf.getMessage());
            // TODO: handle exception
            log.error(fnf.getMessage(), fnf);
        } catch (NoTransformationException nte) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + nte.getMessage());
            // TODO: handle exception
            log.error(nte.getMessage(), nte);
        } catch (OrderService.NoOrderFoundException nofe) {
            Session.lookupSession(sessionID).getLog().onIncoming("ExecutionReport: " + nofe.getMessage());
            // TODO: handle exception
            log.error(nofe.getMessage(), nofe);
        }
    }

}
