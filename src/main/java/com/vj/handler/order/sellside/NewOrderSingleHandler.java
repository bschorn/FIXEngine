package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.EquityOrder;
import com.vj.service.ClientService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.succession.message.NewOrderSingleTransform;
import com.vj.validator.MessageValidator;
import com.vj.validator.ValidatorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix42.NewOrderSingle;

import java.util.List;
import java.util.Optional;

/**
 * Sell-side
 */
public class NewOrderSingleHandler implements MessageHandler<NewOrderSingle> {

    private static final Logger log = LoggerFactory.getLogger(NewOrderSingleHandler.class);

    private final NewOrderSingleTransform newOrderSingleTransform;
    private final List<MessageValidator> validators;

    public NewOrderSingleHandler(NewOrderSingleTransform newOrderSingleTransform, List<MessageValidator> validators) {
        this.newOrderSingleTransform = newOrderSingleTransform;
        this.validators = validators;
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
                log.info(order.toString());
                services().orders().submit(order);
            } catch (FieldNotFound fnf) {
                log.error(fnf.getMessage(), fnf);
            } catch (ClientService.NoClientFoundException ncfe) {
                log.error(ncfe.getMessage(), ncfe);
            } catch (NoTransformationException nte) {
                log.error(nte.getMessage(), nte);
            }
        }
    }

}
