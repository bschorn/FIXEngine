package com.vj.transform.succession.message;

import com.vj.service.ClientService;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;


public interface MessageTransform<T extends quickfix.Message,R>  {
    Class<T> messageClass();
    R inbound(T message, SessionID sessionID, Object...objects) throws FieldNotFound, NoTransformationException, OrderService.NoOrderFoundException, ClientService.NoClientFoundException;
    T outbound(R r) throws NoTransformationException;

    class Unimplemented implements MessageTransform<Message,Object> {
        @Override
        public Class<Message> messageClass() {
            return null;
        }

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
