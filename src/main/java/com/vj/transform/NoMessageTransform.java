package com.vj.transform;

import com.vj.model.entity.EquityOrder;
import com.vj.transform.message.MessageTransform;
import quickfix.Message;
import quickfix.SessionID;

public class NoMessageTransform implements MessageTransform<Message,EquityOrder> {
    @Override
    public Class<Message> messageClass() {
        return null;
    }

    @Override
    public EquityOrder inbound(Message message, SessionID sessionID, Object...objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message outbound(EquityOrder equityOrder) {
        throw new UnsupportedOperationException();
    }
}
