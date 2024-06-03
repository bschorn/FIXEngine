package com.vj.publisher;

import com.vj.manager.SessionManager;
import com.vj.model.entity.Order;
import com.vj.service.OrderService;
import org.slf4j.Logger;
import quickfix.Message;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.OnBehalfOfSubID;

import java.util.Optional;

/**
 *
 * @param <T>
 */
public abstract class OrderPublisher<T extends Order> implements EntityPublisher<T> {



    public static OnBehalfOfCompID onBehalfOfCompID = null;
    public static OnBehalfOfSubID onBehalfOfSubID = null;

    public interface Callback<T> {
        void onSuccess() throws OrderService.NoOrderFoundException;
        void onException(Exception exception);
    }

    protected void send(Message message, Callback callback) {
        if (onBehalfOfCompID != null) {
            message.getHeader().setField(onBehalfOfCompID);
        }
        if (onBehalfOfSubID != null) {
            message.getHeader().setField(onBehalfOfSubID);
        }
        Optional<Exception> optException = SessionManager.sendMessage(message);
        if (optException.isPresent()) {
            callback.onException(optException.get());
        } else {
            try {
                callback.onSuccess();
            } catch (OrderService.NoOrderFoundException nofe) {
                callback.onException(nofe);
            }
        }
    }
}
