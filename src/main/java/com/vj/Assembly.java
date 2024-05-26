package com.vj;

import com.vj.handler.MessageHandlers;
import com.vj.handler.order.buyside.OrderDefaultHandler;
import com.vj.handler.order.buyside.OrderTradeHandler;
import com.vj.handler.order.sellside.NewOrderSingleHandler;
import com.vj.handler.order.sellside.OrderCancelReplaceRequestHandler;
import com.vj.handler.order.sellside.OrderCancelRequestHandler;
import com.vj.publisher.buyside.NewOrderSinglePublisher;
import com.vj.publisher.buyside.OrderCancelReplaceRequestPublisher;
import com.vj.publisher.buyside.OrderCancelRequestPublisher;
import com.vj.publisher.OrderPublishers;
import com.vj.publisher.sellside.ExecutionReportPublisher;
import com.vj.service.ClientService;
import com.vj.service.MarketService;
import com.vj.service.OrderService;
import com.vj.service.ProductService;
import com.vj.service.Services;
import com.vj.transform.NoFieldTransform;
import com.vj.transform.NoMessageTransform;
import com.vj.transform.Transformers;
import com.vj.validator.Validators;
import com.vj.validator.order.equity.NewOrderSingleValidator;
import com.vj.validator.order.equity.OrderCancelReplaceRequestValidator;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelRequest;

/**
 * Manual Dependency Injection
 *
 * The same can be accomplished through Spring.
 *
 */
public class Assembly {

    private final static boolean mocking = true;
    private final static boolean sellside = System.getProperties().containsKey("sellside");
    private final static Assembly INSTANCE = new Assembly();

    private final ClientService clients;
    private final OrderService orders;
    private final ProductService products;
    private final MarketService markets;
    private final MessageHandlers handlers;
    private final OrderPublishers orderPublishers;
    private final Services services;

    private final Transformers transformers = new Transformers(new NoMessageTransform(), new NoFieldTransform());
    private final Validators validators = new Validators();

    private Assembly() {
        orderPublishers = new OrderPublishers();
        if (mocking) {
            clients = new com.vj.mock.ClientServiceImpl();
            if (sellside) {
                orders = new com.vj.mock.SellSideOrderServiceImpl(orderPublishers);
            } else {
                orders = new com.vj.mock.BuySideOrderServiceImpl(orderPublishers);
            }
            products = new com.vj.mock.ProductServiceImpl();
            markets = new com.vj.mock.MarketServiceImpl();
        } else {
            orders = null;
            products = null;
            markets = null;
        }
        handlers = new MessageHandlers();
        services = new Services(clients, orders, products, markets);
        // transformers - fields
        transformers.register(OrdStatus.class, new com.vj.transform.succession.field.OrdStatusTransform());
        transformers.register(OrdType.class, new com.vj.transform.succession.field.OrdTypeTransform());
        transformers.register(Side.class, new com.vj.transform.succession.field.SideTransform());
        transformers.register(SecurityIDSource.class, new com.vj.transform.succession.field.SecurityIDSourceTransform());
        transformers.register(ExDestination.class, new com.vj.transform.succession.field.ExDestinationTransform());
        // transformers - messages
        transformers.register(NewOrderSingle.class, new com.vj.transform.succession.message.NewOrderSingleTransform(services, transformers));
        transformers.register(OrderCancelRequest.class, new com.vj.transform.succession.message.OrderCancelRequestTransform(services, transformers));
        transformers.register(OrderCancelReplaceRequest.class, new com.vj.transform.succession.message.OrderCancelReplaceRequestTransform(services, transformers));
        transformers.register(ExecutionReport.class, new com.vj.transform.succession.message.ExecutionReportTransform(services, transformers));
        // validators - messages
        validators.register(NewOrderSingle.class, new NewOrderSingleValidator());
        validators.register(OrderCancelReplaceRequest.class, new OrderCancelReplaceRequestValidator());
    }

    public static void init() {
        if (sellside) {
            INSTANCE.handlers.register(new NewOrderSingleHandler(INSTANCE.transformers.message(NewOrderSingle.class), INSTANCE.validators.get(NewOrderSingle.class)));
            INSTANCE.handlers.register(new OrderCancelReplaceRequestHandler(INSTANCE.transformers.message(OrderCancelReplaceRequest.class)));
            INSTANCE.handlers.register(new OrderCancelRequestHandler(INSTANCE.transformers.message(OrderCancelRequest.class)));
            INSTANCE.orderPublishers.register(new ExecutionReportPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
        } else {
            INSTANCE.orderPublishers.register(new NewOrderSinglePublisher(INSTANCE.transformers.message(NewOrderSingle.class)));
            INSTANCE.orderPublishers.register(new OrderCancelRequestPublisher(INSTANCE.transformers.message(OrderCancelRequest.class)));
            INSTANCE.orderPublishers.register(new OrderCancelReplaceRequestPublisher(INSTANCE.transformers.message(OrderCancelReplaceRequest.class)));
            INSTANCE.handlers.register(new OrderTradeHandler(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.handlers.register(new OrderDefaultHandler(INSTANCE.transformers.message(ExecutionReport.class)));
        }
    }

    public static Services services() {
        return INSTANCE.services;
    }
    public static MessageHandlers handlers() {
        if (INSTANCE.handlers.size() == 0) {
            init();
        }
        return INSTANCE.handlers;
    }
}
