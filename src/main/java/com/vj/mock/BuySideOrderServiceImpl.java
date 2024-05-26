package com.vj.mock;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.Order;
import com.vj.publisher.OrderPublishers;
import com.vj.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


public class BuySideOrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(BuySideOrderServiceImpl.class);

    private final AtomicLong nextOrderId = new AtomicLong(1000000);

    private final OrderPublishers orderPublishers;
    private final Map<OrderId,LinkedList<Order>> orderHistoryMap = new HashMap<>();
    private final Map<ClientOrderId, Order> clientOrderMap = new HashMap<>();

    public BuySideOrderServiceImpl(OrderPublishers orderPublishers) {
        this.orderPublishers = orderPublishers;
    }

    @Override
    public OrderId nextId() {
        return new OrderId(nextOrderId.getAndIncrement());
    }


    @Override
    public void submit(Order order) {
        if (orderHistoryMap.containsKey(order.id())) {
            throw new RuntimeException("You can not reuse OrderId or submit the same order twice.");
        }
        LinkedList<Order> orderHistory = new LinkedList<>();
        orderHistory.add(order);
        orderHistoryMap.put(order.id(), orderHistory);
        clientOrderMap.put(order.clientOrderId(), order);
        orderPublishers.find(order).publish(order);
    }

    @Override
    public void modify(Order order) {
        if (!clientOrderMap.containsKey(order.clientOrderId())) {
            throw new RuntimeException("OrderService.modify() - order is not known: " + order.clientOrderId());
        }
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
        clientOrderMap.put(order.clientOrderId(), order);

        orderPublishers.find(order).publish(order);
    }

    @Override
    public void update(Order order) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
        clientOrderMap.put(order.clientOrderId(), order);
    }

    @Override
    public Order find(OrderId orderId) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(orderId);
        if (orderHistory != null) {
            return orderHistory.getLast();
        }
        return null;
    }

    @Override
    public Order find(ClientOrderId clientOrderId) {
        return clientOrderMap.get(clientOrderId);
    }

    @Override
    public List<Order> getHistory(OrderId orderId) {
        return orderHistoryMap.get(orderId);
    }

}
