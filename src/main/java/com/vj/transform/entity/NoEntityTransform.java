package com.vj.transform.entity;

import com.vj.model.entity.EquityOrder;
import quickfix.Message;
import quickfix.SessionID;

public class NoEntityTransform implements EntityTransform<Message,EquityOrder>{
    @Override
    public EquityOrder inbound(Message message, SessionID sessionID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message outbound(EquityOrder equityOrder) {
        throw new UnsupportedOperationException();
    }
}
