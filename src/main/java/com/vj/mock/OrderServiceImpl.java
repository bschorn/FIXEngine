package com.vj.mock;

import com.vj.model.attribute.Client;
import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.Order;
import com.vj.publisher.EntityPublisher;
import com.vj.service.OrderService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


public class OrderServiceImpl implements OrderService {

    private final AtomicLong nextOrderId = new AtomicLong(1000000);

    private final Map<Class<?>, EntityPublisher> publisherMap = new HashMap<>();
    private final Map<OrderId,LinkedList<Order>> orderLogMap = new HashMap<>();
    private final Map<Client,List<OrderId>> orderListByClient = new HashMap<>();

    @Override
    public OrderId nextId() {
        return new OrderId(nextOrderId.getAndIncrement());
    }


    @Override
    public void submit(Order order) {
        // create new order log and add this order as the first entry
        {
            LinkedList<Order> orderHistory = new LinkedList<>();
            orderHistory.add(order);
            this.orderLogMap.put(order.id(), orderHistory);
        }
        // add orderId to client's list of orderIds
        {
            List<OrderId> orderIds = this.orderListByClient.getOrDefault(order.client(), new ArrayList<>());
            orderIds.add(order.id());
            this.orderListByClient.put(order.client(), orderIds);
        }
        publisherMap.get(order.getClass()).publish(order);
    }

    @Override
    public void modify(Order order) {
        LinkedList<Order> orderHistory = orderLogMap.get(order.id());
        orderHistory.add(order);
        publisherMap.get(order.getClass()).publish(order);
    }

    @Override
    public void update(Order order) {
        LinkedList<Order> orderHistory = orderLogMap.get(order.id());
        orderHistory.add(order);
    }

    @Override
    public Order find(OrderId orderId) {
        LinkedList<Order> orderHistory = orderLogMap.get(orderId);
        if (orderHistory != null) {
            return orderHistory.getLast();
        }
        return null;
    }

    @Override
    public Order find(ClientOrderId clientOrderId) {
        LinkedList<Order> orderList = orderLogMap.get(clientOrderId.orderId());
        if (orderList != null) {
            return orderList.getLast();
        }
        return null;
    }

    @Override
    public List<Order> getHistory(OrderId orderId) {
        return orderLogMap.get(orderId);
    }

}
