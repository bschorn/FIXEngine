package com.vj.transform.entity;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;


public interface EntityTransform<T extends quickfix.Message,R>  {
    R inbound(T message, SessionID sessionID) throws FieldNotFound;
    T outbound(R r);

    class Unimplemented implements EntityTransform<quickfix.Message,Object> {


        @Override
        public Object inbound(Message message, SessionID sessionID) {
            return null;
        }

        @Override
        public Message outbound(Object o) {
            return null;
        }
    }
}
