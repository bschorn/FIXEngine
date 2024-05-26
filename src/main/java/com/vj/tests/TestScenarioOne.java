package com.vj.tests;

import com.vj.model.attribute.*;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import quickfix.SessionID;

import java.io.IOException;

public class TestScenarioOne {


    Services services;
    SessionID sessionID;
    Account testAccount;
    public TestScenarioOne(Services services, SessionID sessionID, Account testAccount) {
        this.services = services;
        this.sessionID = sessionID;
        this.testAccount = services.clients().lookupAccount(sessionID.getSenderCompID());
    }

    public void run() throws IOException {
        System.out.println("press <enter> to submit order");
        System.in.read();
        // create new order
        OrderId orderId = services.orders().nextId();
        submitOrder(orderId, "MSFT", Side.B, 1000.0, 102.1);

        System.out.println("press <enter> to modify order price");
        System.in.read();

        modifyOrderPrice(orderId, 102.34);

        System.out.println("press <enter> to modify order qty");
        System.in.read();

        modifyOrderQty(orderId, 900.0);

        System.out.println("press <enter> to cancel order");
        System.in.read();

        cancelOrder(orderId);

    }

    public void submitOrder(OrderId orderId, String symbol, Side side, double qty, double px) {
        EquityOrder newOrder = EquityOrder.create(orderId, new Client(sessionID.getTargetCompID()))
                .account(testAccount)
                .instrument(services.products().find(InstrumentSource.RIC, symbol))
                .exchange(Exchange.NYSE)
                .orderType(OrderType.LIMIT)
                .side(side)
                .orderQty(qty)
                .limitPrice(px)
                .end();
        System.out.println("Submitting Order");
        System.out.println(newOrder.toString());
        // submit to services (which sends it to broker)
        services.orders().submit(newOrder);
    }

    private void modifyOrderPrice(OrderId orderId, double px) {
        // retrieve current version of order (after sent to broker)
        EquityOrder currentOrder = services.orders().find(orderId);
        modifyOrder(orderId, currentOrder.orderQty(), px);
    }

    private void modifyOrderQty(OrderId orderId, double qty) {
        // retrieve current version of order (after sent to broker)
        EquityOrder currentOrder = services.orders().find(orderId);
        modifyOrder(orderId, qty, currentOrder.limitPrice());
    }

    private void modifyOrder(OrderId orderId, double qty, double px) {
        // retrieve current version of order (after sent to broker)
        EquityOrder currentOrder = services.orders().find(orderId);
        System.out.println("Current Order");
        System.out.println(currentOrder.toString());

        EquityOrder modifiedOrder = null;
        // modify current version
        if (currentOrder.limitPrice() != px && currentOrder.orderQty() != qty) {
            modifiedOrder = currentOrder.modify()
                    .orderAction(OrderAction.REPLACE)
                    .orderQty(qty)
                    .limitPrice(px)
                    .end();
        } else if (currentOrder.limitPrice() != px) {
            modifiedOrder = currentOrder.modify()
                    .orderAction(OrderAction.REPLACE)
                    .limitPrice(px)
                    .end();
        } else if (currentOrder.orderQty() != qty) {
            modifiedOrder = currentOrder.modify()
                    .orderAction(OrderAction.REPLACE)
                    .orderQty(qty)
                    .end();
        } else {
            return;
        }

        System.out.println("Modifying Order");
        System.out.println(modifiedOrder.toString());
        // submit to services the modified order (which sends it to broker)
        services.orders().modify(modifiedOrder);
    }

    private void cancelOrder(OrderId orderId) {
        // retrieve current version of order
        EquityOrder currentOrder = services.orders().find(orderId);
        // cancel current version
        EquityOrder canOrder = currentOrder.modify().orderAction(OrderAction.CANCEL).end();
        System.out.println("Cancelling Order");
        System.out.println(canOrder.toString());
        // submit to services (which sends it to broker)
        services.orders().modify(canOrder);

    }
}
