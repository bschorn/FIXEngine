package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.entity.EquityStateTransform;
import com.vj.transform.entity.NewOrderSingleTransform;
import com.vj.validator.MessageValidator;
import com.vj.validator.ValidatorResult;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.SecurityType;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;

import java.util.List;
import java.util.Optional;

public class NewOrderSingleHandler implements MessageHandler<NewOrderSingle> {

    private final SecurityType SecurityType = new SecurityType();
    private final NewOrderSingleTransform inboundTransform;
    private final EquityStateTransform outboundTransform;
    private final List<MessageValidator> validators;

    public NewOrderSingleHandler(NewOrderSingleTransform inboundTransform, EquityStateTransform outboundTransform, List<MessageValidator> validators) {
        this.inboundTransform = inboundTransform;
        this.outboundTransform = outboundTransform;
        this.validators = validators;
    }

    @Override
    public boolean test(NewOrderSingle message, SessionID sessionID) {
        try {
            if (message.isSet(SecurityType)) {
                SecurityType securityType = message.getSecurityType();
                return (!securityType.getValue().equalsIgnoreCase(SecurityType.US_TREASURY_BILL) && !securityType.getValue().equalsIgnoreCase(SecurityType.US_TREASURY_BILL_1));
            }
        } catch (FieldNotFound fnf) {
            // we already checked isSet
            fnf.printStackTrace();
        }
        return true;
    }

    @Override
    public String msgType() {
        return NewOrderSingle.MSGTYPE;
    }

    @Override
    public void handle(NewOrderSingle message, SessionID sessionID) {
        try {
            Optional<ValidatorResult> optNotOKResult = validators.stream()
                                                           .map(v -> v.apply(message, sessionID))
                                                           .filter(vr -> vr.resultType() != ValidatorResult.ResultType.OK)
                                                           .findFirst();
            if (optNotOKResult.isPresent()) {
                rejectMessage(message, sessionID, optNotOKResult.get());
            } else {
                OrderId orderId = services().orders().nextId();
                acceptMessage(message, sessionID, orderId);
                EquityOrder order = inboundTransform.inbound(message, sessionID, orderId);
                // anything else before submitting goes here
                Session.lookupSession(sessionID).getLog().onIncoming(order.toString());
                // TODO: submit should return a value indicating accept/reject of order (and why)
                // OrderResult orderResult =
                    services().orders().submit(order);
                // switch (orderResult.resultType) {
                //  case OK:
                    EquityOrder acceptedOrder = services().orders().find(order.id());
                    acceptOrder(acceptedOrder, sessionID);
                //  break;
                //  default:
                //      rejectOrder(order, sessionID);
                //}
            }
        } catch (FieldNotFound | SessionNotFound fieldNotFound) {
            // TODO: respond to exception
        }
    }

    /**
     * REJECT the FIX message OrdStatus=REJECT
     * (rejected based on the format of the message)
     *
     * @param message
     * @param sessionID
     * @param validatorResult
     */
    private void rejectMessage(NewOrderSingle message, SessionID sessionID, ValidatorResult validatorResult) throws FieldNotFound, SessionNotFound {
        // TODO: send ExecutionReport
    }

    /**
     * REJECT the EquityOrder OrdStatus=REJECT
     * (rejected based on content of the message)
     *
     * @param equityOrder
     * @param sessionID
     */
    private void rejectOrder(EquityOrder equityOrder, SessionID sessionID) throws FieldNotFound, SessionNotFound {
        // TODO: send ExecutionReport
    }

    /**
     * ACK the order OrdStatus=PENDING_NEW
     *
     * @param message
     * @param sessionID
     */
    private void acceptMessage(NewOrderSingle message, SessionID sessionID, OrderId orderId) throws FieldNotFound, SessionNotFound {
        ExecutionReport executionReport = new ExecutionReport();
        executionReport.set(new OrderID(orderId.toString()));
        executionReport.set(message.getClOrdID());
        executionReport.set(new OrdStatus(OrdStatus.PENDING_NEW));
        Session.sendToTarget(executionReport, sessionID);
    }

    /**
     * ACCEPT the order OrderStatus=NEW
     *
     * @param equityOrder
     * @param sessionID
     */
    private void acceptOrder(EquityOrder equityOrder, SessionID sessionID) throws FieldNotFound, SessionNotFound {
        Session.sendToTarget(outboundTransform.outbound(equityOrder), sessionID);
    }
}
