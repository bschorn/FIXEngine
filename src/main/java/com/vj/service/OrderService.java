package com.vj.service;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.Order;

import java.util.List;

public interface OrderService {

    OrderId nextId();
    void submit(Order order);
    void modify(Order order);
    void update(Order order);
    <T extends Order> T find(OrderId orderId);
    <T extends Order> T find(ClientOrderId clientOrderId);
    <T extends Order> List<T> getHistory(OrderId orderId);

}
