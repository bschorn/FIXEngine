package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.succession.message.OrderCancelRequestTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix42.OrderCancelRequest;

public class OrderCancelRequestHandler implements MessageHandler<OrderCancelRequest> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelRequestHandler.class);

    private final OrderCancelRequestTransform orderCancelRequestTransform;

    public OrderCancelRequestHandler(OrderCancelRequestTransform orderCancelRequestTransform) {
        this.orderCancelRequestTransform = orderCancelRequestTransform;
    }

    @Override
    public String msgType() {
        return OrderCancelRequest.MSGTYPE;
    }

    @Override
    public void handle(OrderCancelRequest message, SessionID sessionID) {
        try {
            EquityOrder equityOrder = orderCancelRequestTransform.inbound(message, sessionID);
            services().orders().modify(equityOrder.modify(OrderAction.CANCEL));
        } catch (FieldNotFound fnf) {
            // TODO: handle exception
            log.error(fnf.getMessage(), fnf);
        }
    }
}
