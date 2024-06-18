package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.entity.EquityOrder;
import com.vj.service.ClientService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.message.NewOrderSingleTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix42.NewOrderSingle;

/**
 * Sell-side
 */
public class NewOrderSingleHandler implements MessageHandler<NewOrderSingle> {

    private static final Logger log = LoggerFactory.getLogger(NewOrderSingleHandler.class);

    private final NewOrderSingleTransform newOrderSingleTransform;

    public NewOrderSingleHandler(NewOrderSingleTransform newOrderSingleTransform) {
        this.newOrderSingleTransform = newOrderSingleTransform;
    }

    @Override
    public String msgType() {
        return NewOrderSingle.MSGTYPE;
    }

    @Override
    public void handle(NewOrderSingle message, SessionID sessionID) {
        try {
            EquityOrder order = newOrderSingleTransform.inbound(message, sessionID);
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
