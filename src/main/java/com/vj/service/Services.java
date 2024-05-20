package com.vj.service;

public class Services {

    private final ClientService clients;
    private final OrderService orders;
    private final ProductService products;
    private final MarketService markets;

    public Services(ClientService clients, OrderService orders, ProductService products, MarketService markets) {
        this.clients = clients;
        this.orders = orders;
        this.products = products;
        this.markets = markets;
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
