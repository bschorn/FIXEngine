package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.succession.message.OrderCancelReplaceRequestTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix42.OrderCancelReplaceRequest;

public class OrderCancelReplaceRequestHandler implements MessageHandler<OrderCancelReplaceRequest> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelReplaceRequestHandler.class);

    private final OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform;

    public OrderCancelReplaceRequestHandler(OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform) {
        this.orderCancelReplaceRequestTransform = orderCancelReplaceRequestTransform;
    }

    @Override
    public String msgType() {
        return OrderCancelReplaceRequest.MSGTYPE;
    }

    /**
     * Sell-side
     */
    @Override
    public void handle(OrderCancelReplaceRequest message, SessionID sessionID) {
        try {
            double newOrderQty = message.getOrderQty().getValue();
            double newPrice = message.getPrice().getValue();
            EquityOrder equityOrder = orderCancelReplaceRequestTransform.inbound(message, sessionID);

            // check that newOrderQty isn't less than currently filled
            if (newOrderQty < equityOrder.filledQty()) {
                // set newOrderQty to equal filledQty and close order
                newOrderQty = equityOrder.filledQty();
                newPrice = 0;
            }
            EquityOrder modifiedOrder = equityOrder.modify()
                    .orderQty(newOrderQty)
                    .limitPrice(newPrice > 0 ? newPrice : equityOrder.limitPrice())
                    .orderAction(OrderAction.REPLACE)
                    .end();
            services().orders().modify(modifiedOrder);
        } catch (FieldNotFound fnf) {
            // TODO: handle exception
            log.error(fnf.getMessage(), fnf);
        }

    }
}
