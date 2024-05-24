package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.message.NewOrderSingleTransform;
import com.vj.validator.MessageValidator;
import com.vj.validator.ValidatorResult;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.SecurityType;
import quickfix.fix42.NewOrderSingle;

import java.util.List;
import java.util.Optional;

public class NewOrderSingleHandler implements MessageHandler<NewOrderSingle> {

    private final SecurityType SecurityType = new SecurityType();
    private final NewOrderSingleTransform newOrderSingleTransform;
    private final List<MessageValidator> validators;

    public NewOrderSingleHandler(NewOrderSingleTransform newOrderSingleTransform, List<MessageValidator> validators) {
        this.newOrderSingleTransform = newOrderSingleTransform;
        this.validators = validators;
    }

    @Override
    public boolean test(NewOrderSingle message, SessionID sessionID) {
        try {
            if (message.isSet(SecurityType)) {
                SecurityType securityType = message.getSecurityType();
                return (!securityType.getValue().equalsIgnoreCase(quickfix.field.SecurityType.US_TREASURY_BILL));
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
        Optional<ValidatorResult> optNotOKResult = validators.stream()
                .map(v -> v.apply(message, sessionID))
                .filter(vr -> vr.resultType() != ValidatorResult.ResultType.OK)
                .findFirst();
        if (optNotOKResult.isPresent() && optNotOKResult.get().result().isPresent()) {
            System.err.println(optNotOKResult.get().result().get());
        } else {
            OrderId orderId = services().orders().nextId();
            try {
                EquityOrder order = newOrderSingleTransform.inbound(message, sessionID, orderId);
                System.out.println(order);
                services().orders().submit(order);
            } catch (FieldNotFound fnf) {
                fnf.printStackTrace();
            }
        }
    }

}
