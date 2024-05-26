package com.vj.publisher;

import com.vj.manager.SessionManager;
import com.vj.model.entity.Order;
import quickfix.Message;

import java.util.Optional;

public abstract class OrderPublisher<T extends Order> implements EntityPublisher<T> {


    public interface Callback {
        void onSuccess();
        void onException(Exception exception);
    }

    protected void send(Message message, Callback callback) {
        Optional<Exception> optException = SessionManager.sendMessage(message);
        if (optException.isPresent()) {
            callback.onException(optException.get());
        } else {
            callback.onSuccess();
        }
    }
}
