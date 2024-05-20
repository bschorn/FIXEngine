package com.vj.publisher;

import com.vj.model.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderPublishers {

    private final List<OrderPublisher> publisherList = new ArrayList<>();

    public OrderPublisher find(Order order) {
        for (int i = 0; i < publisherList.size(); i++) {
            if (publisherList.get(i).test(order)) {
                return publisherList.get(i);
            }
        }
        return null;
    }

    public void register(OrderPublisher orderPublisher) {
        publisherList.add(orderPublisher);
    }

}
