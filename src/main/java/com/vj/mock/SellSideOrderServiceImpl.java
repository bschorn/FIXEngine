package com.vj.mock;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.Order;
import com.vj.publisher.OrderPublishers;
import com.vj.service.OrderService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SellSideOrderServiceImpl implements OrderService {

    private final AtomicLong nextOrderId = new AtomicLong(2000000);

    private final OrderPublishers orderPublishers;
    private final Map<OrderId, LinkedList<Order>> orderHistoryMap = new HashMap<>();
    private final Map<ClientOrderId, Order> clientOrderMap = new HashMap<>();

    public SellSideOrderServiceImpl(OrderPublishers orderPublishers) {
        this.orderPublishers = orderPublishers;
    }

    @Override
    public OrderId nextId() {
        return new OrderId(nextOrderId.getAndIncrement());
    }

    @Override
    public void submit(Order order) {
        LinkedList<Order> orderHistory = new LinkedList<>();
        orderHistory.add(order);
        this.orderHistoryMap.put(order.id(), orderHistory);
        this.clientOrderMap.put(order.clientOrderId(), order);
        orderPublishers.find(order).publish(order);
    }

    @Override
    public void modify(Order order) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
        this.clientOrderMap.put(order.clientOrderId(), order);
        orderPublishers.find(order).publish(order);
    }

    @Override
    public void update(Order order) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
        this.clientOrderMap.put(order.clientOrderId(), order);
    }

    @Override
    public <T extends Order> T find(ClientOrderId clientOrderId) {
        return (T) clientOrderMap.get(clientOrderId);
    }

    @Override
    public <T extends Order> List<T> getHistory(OrderId orderId) {
        return (List<T>) orderHistoryMap.get(orderId);
    }

    @Override
    public Order find(OrderId orderId) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(orderId);
        if (orderHistory != null) {
            return orderHistory.getLast();
        }
        return null;
    }

}
