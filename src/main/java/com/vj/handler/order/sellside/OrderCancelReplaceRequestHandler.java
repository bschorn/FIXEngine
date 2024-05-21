package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import quickfix.SessionID;
import quickfix.fix44.OrderCancelReplaceRequest;

public class OrderCancelReplaceRequestHandler implements MessageHandler<OrderCancelReplaceRequest> {

    @Override
    public String msgType() {
        return OrderCancelReplaceRequest.MSGTYPE;
    }
    @Override
    public void handle(OrderCancelReplaceRequest message, SessionID sessionID) {
        //TODO

    }
}
