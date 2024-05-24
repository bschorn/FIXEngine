package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.message.OrderCancelRequestTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix42.OrderCancelRequest;

public class OrderCancelRequestHandler implements MessageHandler<OrderCancelRequest> {


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
            fnf.printStackTrace();
        }
    }
}
