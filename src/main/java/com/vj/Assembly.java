package com.vj;

import com.vj.handler.MessageHandlers;
import com.vj.handler.order.buyside.OrderDefaultHandler;
import com.vj.handler.order.buyside.OrderTradeHandler;
import com.vj.mock.MarketServiceImpl;
import com.vj.model.attribute.Client;
import com.vj.model.attribute.InstrumentSource;
import com.vj.model.attribute.OrderState;
import com.vj.model.attribute.OrderType;
import com.vj.model.attribute.Side;
import com.vj.publisher.NewOrderSinglePublisher;
import com.vj.publisher.OrderCancelReplaceRequestPublisher;
import com.vj.publisher.OrderCancelRequestPublisher;
import com.vj.publisher.OrderPublishers;
import com.vj.service.ClientService;
import com.vj.service.MarketService;
import com.vj.service.OrderService;
import com.vj.service.ProductService;
import com.vj.service.Services;
import com.vj.transform.Transformers;
import com.vj.transform.attribute.InstrumentSourceTransform;
import com.vj.transform.attribute.NoAttributeTransform;
import com.vj.transform.attribute.OrderStateTransform;
import com.vj.transform.attribute.OrderTypeTransform;
import com.vj.transform.attribute.SideTransform;
import com.vj.transform.entity.*;
import com.vj.transform.identifier.ClientTransform;
import com.vj.transform.identifier.NoIdentifierTransform;
import com.vj.validator.Validators;
import com.vj.validator.order.equity.NewOrderSingleValidator;
import com.vj.validator.order.equity.OrderCancelReplaceRequestValidator;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelReplaceRequest;
import quickfix.fix44.OrderCancelRequest;

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

    private final Transformers transformers = new Transformers(new NoEntityTransform(), new NoAttributeTransform(), new NoIdentifierTransform());
    private final Validators validators = new Validators();

    private Assembly() {
        if (mocking) {
            clients = new com.vj.mock.ClientServiceImpl();
            if (sellside) {
                orders = new com.vj.mock.SellSideOrderServiceImpl();
            } else {
                orders = new com.vj.mock.OrderServiceImpl();
            }
            products = new com.vj.mock.ProductServiceImpl();
            markets = new MarketServiceImpl();
        } else {
            orders = null;
            products = null;
            markets = null;
        }
        handlers = new MessageHandlers();
        orderPublishers = new OrderPublishers();
        services = new Services(clients, orders, products, markets);
        transformers.register(OrderState.class, new OrderStateTransform());
        transformers.register(OrderType.class, new OrderTypeTransform());
        transformers.register(Side.class, new SideTransform());
        transformers.register(InstrumentSource.class, new InstrumentSourceTransform());
        transformers.register(Client.class, new ClientTransform());
        transformers.register(NewOrderSingle.class, new NewOrderSingleTransform(services, transformers));
        transformers.register(OrderCancelRequest.class, new OrderCancelRequestTransform(services, transformers));
        transformers.register(OrderCancelReplaceRequest.class, new OrderCancelReplaceRequestTransform(services, transformers));
        transformers.register(ExecutionReport.class, new EquityStateTransform(services, transformers));
        validators.register(NewOrderSingle.class, new NewOrderSingleValidator());
        validators.register(OrderCancelReplaceRequest.class, new OrderCancelReplaceRequestValidator());
    }

    public static void init() {
        INSTANCE.handlers.register(new OrderTradeHandler(INSTANCE.transformers.entity(ExecutionReport.class)));
        INSTANCE.handlers.register(new OrderDefaultHandler(INSTANCE.transformers.entity(ExecutionReport.class)));
        INSTANCE.handlers.register(new com.vj.handler.order.sellside.NewOrderSingleHandler(
                INSTANCE.transformers.entity(NewOrderSingle.class),
                INSTANCE.transformers.entity(ExecutionReport.class),
                INSTANCE.validators.get(NewOrderSingle.class)));
        INSTANCE.handlers.register(new com.vj.handler.order.sellside.OrderCancelReplaceRequestHandler());
        INSTANCE.handlers.register(new com.vj.handler.order.sellside.OrderCancelRequestHandler());

        INSTANCE.orderPublishers.register(new NewOrderSinglePublisher(INSTANCE.transformers.entity(NewOrderSingle.class)));
        INSTANCE.orderPublishers.register(new OrderCancelRequestPublisher(INSTANCE.transformers.entity(OrderCancelRequest.class)));
        INSTANCE.orderPublishers.register(new OrderCancelReplaceRequestPublisher(INSTANCE.transformers.entity(OrderCancelReplaceRequest.class)));
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
