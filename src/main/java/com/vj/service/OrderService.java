package com.vj.service;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.Order;

import java.util.List;

public interface OrderService {

    OrderId nextId();
    void submit(Order order);
    void modify(Order order) throws NoOrderFoundException;
    void update(Order order) throws NoOrderFoundException;
    <T extends Order> T find(OrderId orderId) throws NoOrderFoundException;
    <T extends Order> T find(ClientOrderId clientOrderId) throws NoOrderFoundException;
    <T extends Order> List<T> getHistory(OrderId orderId) throws NoOrderFoundException;

    class NoOrderFoundException extends Exception {
        public NoOrderFoundException(String message) {
            super(message);
        }
    }
}
