package com.vj.publisher;

import com.vj.manager.SessionManager;
import com.vj.model.entity.Order;
import quickfix.Message;

import java.util.Optional;

public abstract class OrderPublisher<T extends Order> implements EntityPublisher<T> {


    protected void send(Message message) throws Exception {
        Optional<Exception> optException = SessionManager.sendMessage(message);
        if (optException.isPresent()) {
            throw optException.get();
        }

    }
}
