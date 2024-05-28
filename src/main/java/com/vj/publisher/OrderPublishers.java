package com.vj.publisher;

import com.vj.model.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrderPublishers {

    private static final Logger log = LoggerFactory.getLogger(OrderPublishers.class);

    private final List<OrderPublisher> publisherList = new ArrayList<>();

    public OrderPublisher find(Order order) {
        for (int i = 0; i < publisherList.size(); i++) {
            if (publisherList.get(i).isPublisher(order)) {
                OrderPublisher orderPublisher = publisherList.get(i);
                log.info(this.getClass().getSimpleName() + ".find() - " + orderPublisher.getClass().getSimpleName() + " to publish: " + order);
                return publisherList.get(i);
            }
        }
        return null;
    }

    public void register(OrderPublisher orderPublisher) {
        log.info(this.getClass().getSimpleName() + ".register() - Register: orderPublisher[" + orderPublisher + "]");
        publisherList.add(orderPublisher);
    }

}
