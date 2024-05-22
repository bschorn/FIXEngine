package com.vj.transform.message;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;


public interface MessageTransform<T extends quickfix.Message,R>  {
    R inbound(T message, SessionID sessionID, Object...objects) throws FieldNotFound;
    T outbound(R r);

    class Unimplemented implements MessageTransform<Message,Object> {
        @Override
        public Object inbound(Message message, SessionID sessionID, Object...objects) {
            return null;
        }

        @Override
        public Message outbound(Object o) {
            return null;
        }
    }
}
