package com.vj;

import com.vj.handler.MessageHandlers;
import com.vj.handler.order.buyside.*;
import com.vj.handler.order.sellside.NewOrderSingleHandler;
import com.vj.handler.order.sellside.OrderCancelReplaceRequestHandler;
import com.vj.handler.order.sellside.OrderCancelRequestHandler;
import com.vj.publisher.buyside.NewOrderSinglePublisher;
import com.vj.publisher.buyside.OrderCancelReplaceRequestPublisher;
import com.vj.publisher.buyside.OrderCancelRequestPublisher;
import com.vj.publisher.OrderPublishers;
import com.vj.publisher.sellside.*;
import com.vj.service.ClientService;
import com.vj.service.MarketService;
import com.vj.service.OrderService;
import com.vj.service.ProductService;
import com.vj.service.Services;
import com.vj.transform.NoFieldTransform;
import com.vj.transform.NoMessageTransform;
import com.vj.transform.Transformers;
import com.vj.validator.Validators;
import quickfix.fix42.*;

/**
 * Manual Dependency Injection
 *
 * The same can be accomplished through Spring.
 *
 */
public class Assembly {

    private final static boolean mocking = true;
    public final static boolean sellside = Boolean.valueOf(System.getProperty("sellside","false"));
    private final static Assembly INSTANCE = new Assembly();

    private final ClientService clients;
    private final OrderService orders;
    private final ProductService products;
    private final MarketService markets;
    private final MessageHandlers handlers;
    private final OrderPublishers publishers;
    private final Services services;

    private final Transformers transformers = new Transformers(new NoMessageTransform(), new NoFieldTransform());
    private final Validators validators = new Validators();

    private Assembly() {
        publishers = new OrderPublishers();
        if (mocking) {
            clients = new com.vj.mock.ClientServiceImpl();
            if (sellside) {
                orders = new com.vj.mock.SellSideOrderServiceImpl(publishers);
            } else {
                orders = new com.vj.mock.BuySideOrderServiceImpl(publishers);
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

        String broker = System.getProperty("broker");
        if (broker.equalsIgnoreCase("IBKR")) {
            com.vj.brokers.ibkr.TransformAssembly.assemble(services, transformers);
        } else if (broker.equalsIgnoreCase("Succession")) {
            com.vj.brokers.succession.TransformAssembly.assemble(services, transformers);
        }
        // validators - messages
        //validators.register(NewOrderSingle.class, new NewOrderSingleValidator());
        //validators.register(OrderCancelReplaceRequest.class, new OrderCancelReplaceRequestValidator());
    }

    public static void init() {
        if (sellside) {
            INSTANCE.handlers.register(new NewOrderSingleHandler(INSTANCE.transformers.message(NewOrderSingle.class)));
            INSTANCE.handlers.register(new OrderCancelReplaceRequestHandler(INSTANCE.transformers.message(OrderCancelReplaceRequest.class)));
            INSTANCE.handlers.register(new OrderCancelRequestHandler(INSTANCE.transformers.message(OrderCancelRequest.class)));
            INSTANCE.publishers.register(new OrderAcceptedPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.publishers.register(new OrderRejectedPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.publishers.register(new AckCancelOrderPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.publishers.register(new OrderCancelledPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.publishers.register(new OrderReplacedPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.publishers.register(new OrderTradedPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
            //INSTANCE.orderPublishers.register(new ExecutionReportPublisher(INSTANCE.transformers.message(ExecutionReport.class)));
        } else {
            INSTANCE.publishers.register(new NewOrderSinglePublisher(INSTANCE.transformers.message(NewOrderSingle.class)));
            INSTANCE.publishers.register(new OrderCancelRequestPublisher(INSTANCE.transformers.message(OrderCancelRequest.class)));
            INSTANCE.publishers.register(new OrderCancelReplaceRequestPublisher(INSTANCE.transformers.message(OrderCancelReplaceRequest.class)));
            INSTANCE.handlers.register(new OrderAcceptHandler(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.handlers.register(new OrderTradeHandler(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.handlers.register(new OrderReplaceAcceptHandler(INSTANCE.transformers.message(ExecutionReport.class)));
            INSTANCE.handlers.register(new OrderCancelAcceptHandler(INSTANCE.transformers.message(ExecutionReport.class)));
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
