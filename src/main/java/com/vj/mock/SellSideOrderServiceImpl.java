package com.vj.mock;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.Order;
import com.vj.publisher.OrderPublishers;
import com.vj.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Sell-side implementation
 */
public class SellSideOrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(SellSideOrderServiceImpl.class);

    private final AtomicLong nextOrderId = new AtomicLong(2000000);

    private final OrderPublishers orderPublishers;
    //private final Map<OrderId, LinkedList<Order>> orderHistoryMap = new HashMap<>();
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
        log.info(this.getClass().getSimpleName() + ".submit() - " + order);
        this.clientOrderMap.put(order.clientOrderId(), order);
        orderPublishers.find(order).publish(order);
    }

    @Override
    public void modify(Order order) {
        log.info(this.getClass().getSimpleName() + ".modify() - " + order);
        this.clientOrderMap.put(order.clientOrderId(), order);
        orderPublishers.find(order).publish(order);
    }

    @Override
    public void update(Order order) {
        log.info(this.getClass().getSimpleName() + ".update() - " + order);
        this.clientOrderMap.put(order.clientOrderId(), order);
    }

    @Override
    public <T extends Order> T find(ClientOrderId clientOrderId) throws NoOrderFoundException {
        T t = (T) clientOrderMap.get(clientOrderId);
        if (t != null) {
            return t;
        }
        throw new NoOrderFoundException(this.getClass().getSimpleName() + ".find() - no order found for ClientOrderId: " + clientOrderId.toString());
    }

    @Override
    public <T extends Order> List<T> getHistory(OrderId orderId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Order> getOpenOrders() {
        return clientOrderMap.values().stream()
                .filter(o -> o.isOpen())
                .collect(Collectors.toList());
    }

    @Override
    public Order find(OrderId orderId) {
        throw new UnsupportedOperationException();
    }

}
