package com.vj.mock;

import com.vj.model.attribute.Client;
import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.Order;
import com.vj.service.OrderService;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SellSideOrderServiceImpl implements OrderService {

    private final AtomicLong nextOrderId = new AtomicLong(2000000);

    private final Map<OrderId,LinkedList<Order>> orderHistoryMap = new HashMap<>();
    private final Map<Client,Set<OrderId>> clientOrderMap = new HashMap<>();

    @Override
    public OrderId nextId() {
        return new OrderId(nextOrderId.getAndIncrement());
    }

    @Override
    public void submit(Order order) {
        LinkedList<Order> orderHistory = new LinkedList<>();
        orderHistory.add(order);
        this.orderHistoryMap.put(order.id(), orderHistory);
        Set<OrderId> clientOrderIds = new HashSet<>();
        clientOrderIds.add(order.id());
        this.clientOrderMap.put(order.client(), clientOrderIds);
    }

    @Override
    public void modify(Order order) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
    }

    @Override
    public void update(Order order) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
    }

    @Override
    public <T extends Order> T find(ClientOrderId clientOrderId) {
        return null;
    }

    @Override
    public <T extends Order> List<T> getHistory(OrderId orderId) {
        return null;
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
