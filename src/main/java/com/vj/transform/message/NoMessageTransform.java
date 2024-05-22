package com.vj.transform.message;

import com.vj.model.entity.EquityOrder;
import quickfix.Message;
import quickfix.SessionID;

public class NoMessageTransform implements MessageTransform<Message,EquityOrder> {
    @Override
    public EquityOrder inbound(Message message, SessionID sessionID, Object...objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message outbound(EquityOrder equityOrder) {
        throw new UnsupportedOperationException();
    }
}
