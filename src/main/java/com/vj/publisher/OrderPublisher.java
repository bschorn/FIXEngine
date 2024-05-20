package com.vj.publisher;

import com.vj.manager.SessionManager;
import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.Order;
import quickfix.Message;

import java.util.Optional;

public abstract class OrderPublisher<T extends Order> implements EntityPublisher<T> {


    protected void send(Message message, Order order) {
        Optional<Exception> optException = SessionManager.sendMessage(message);
        if (optException.isPresent()) {
            Exception sendException = optException.get();
            //TODO deal with exception
        } else {
            // Message Sent
            // Update Order Service with a new version of EquityOrder with OrderAction set to NONE
            services().orders().update(order.modify(OrderAction.NONE));
        }

    }
}
