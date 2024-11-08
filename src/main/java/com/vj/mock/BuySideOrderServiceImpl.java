package com.vj.mock;

import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderId;
import com.vj.model.entity.Order;
import com.vj.publisher.OrderPublishers;
import com.vj.service.OrderService;
import com.vj.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.*;


public class BuySideOrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(BuySideOrderServiceImpl.class);

    private final OrderPublishers orderPublishers;
    private final Map<OrderId, LinkedList<Order>> orderHistoryMap = new HashMap<>();
    private final Map<ClientOrderId, Order> clientOrderMap = new HashMap<>();

    public BuySideOrderServiceImpl(OrderPublishers orderPublishers) {
        this.orderPublishers = orderPublishers;
    }

    @Override
    public OrderId nextId() {
        return new OrderId(IdGenerator.nextId());
    }


    @Override
    public void submit(Order order) {
        log.info(this.getClass().getSimpleName() + ".submit() - " + order);
        if (orderHistoryMap.containsKey(order.id())) {
            throw new RuntimeException("You can not reuse OrderId or submit the same order twice.");
        }
        LinkedList<Order> orderHistory = new LinkedList<>();
        orderHistory.add(order);
        orderHistoryMap.put(order.id(), orderHistory);
        clientOrderMap.put(order.clientOrderId(), order);
        orderPublishers.find(order).publish(order);
    }

    /**
     * Modify records the event and publishes.
     * <p>
     * Modify is for changes that also need to be published to Broker.
     */
    @Override
    public void modify(Order order) {
        log.info(this.getClass().getSimpleName() + ".modify() - " + order);
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        if (orderHistory == null) {
            throw new RuntimeException("OrderService.modify() - order is not known, OrderId: " + order.id().toString());
        }
        orderHistory.add(order);
        clientOrderMap.put(order.clientOrderId(), order);

        orderPublishers.find(order).publish(order);
    }

    /**
     * Update records the event received.
     * <p>
     * Update is for changes that either came from the Broker or are not to be sent to the Broker.
     */
    @Override
    public void update(Order order) {
        log.info(this.getClass().getSimpleName() + ".update() - " + order);
        LinkedList<Order> orderHistory = orderHistoryMap.get(order.id());
        orderHistory.add(order);
        clientOrderMap.put(order.clientOrderId(), order);
    }

    @Override
    public List<Order> getOpenOrders() {
        return clientOrderMap.values().stream()
                .filter(o -> o.isOpen())
                .collect(Collectors.toList());
    }

    /**
     * Find the order using the OrderId key
     */
    @Override
    public Order find(OrderId orderId) {
        LinkedList<Order> orderHistory = orderHistoryMap.get(orderId);
        if (orderHistory != null) {
            return orderHistory.getLast();
        }
        return null;
    }

    /**
     * Find the order using the ClientOrderId key
     */
    @Override
    public Order find(ClientOrderId clientOrderId) {
        return clientOrderMap.get(clientOrderId);
    }

    /**
     * Get a events relating to an OrderId (all the versions/instances of Order for an OrderId)
     */
    @Override
    public List<Order> getHistory(OrderId orderId) {
        return orderHistoryMap.get(orderId);
    }

}
