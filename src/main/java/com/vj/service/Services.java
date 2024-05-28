package com.vj.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Services {

    private static final Logger log = LoggerFactory.getLogger(Services.class);

    private final ClientService clients;
    private final OrderService orders;
    private final ProductService products;
    private final MarketService markets;

    public Services(ClientService clients, OrderService orders, ProductService products, MarketService markets) {
        this.clients = clients;
        this.orders = orders;
        this.products = products;
        this.markets = markets;
        log.info(this.getClass().getSimpleName() + ".ctor() - ClientService: " + clients.getClass().getSimpleName());
        log.info(this.getClass().getSimpleName() + ".ctor() - OrderService: " + orders.getClass().getSimpleName());
        log.info(this.getClass().getSimpleName() + ".ctor() - ProductService: " + products.getClass().getSimpleName());
        log.info(this.getClass().getSimpleName() + ".ctor() - MarketService: " + markets.getClass().getSimpleName());
    }

    public ClientService clients() {
        return clients;
    }
    public OrderService orders() {
        return orders;
    }
    public ProductService products() {
        return products;
    }
    public MarketService markets() {
        return markets;
    }
}
