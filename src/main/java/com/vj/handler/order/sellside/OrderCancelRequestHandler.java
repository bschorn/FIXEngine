package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import quickfix.SessionID;
import quickfix.fix44.OrderCancelRequest;

public class OrderCancelRequestHandler implements MessageHandler<OrderCancelRequest> {



    @Override
    public String msgType() {
        return OrderCancelRequest.MSGTYPE;
    }

    @Override
    public void handle(OrderCancelRequest message, SessionID sessionID) {
        //TODO
    }
}
