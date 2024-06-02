package com.vj.interactive;

import com.vj.model.attribute.*;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Order;
import com.vj.service.ClientService;
import com.vj.service.OrderService;
import com.vj.service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionID;

/**
 *
 */
public class CommandLineSession {

    private static final Logger log = LoggerFactory.getLogger(CommandLineSession.class);
    Services services;
    SessionID sessionID;
    Account testAccount;

    public CommandLineSession(Services services, SessionID sessionID) {
        this.services = services;
        this.sessionID = sessionID;
        try {
            this.testAccount = services.clients().lookupAccount(sessionID.getSenderCompID());
        } catch (ClientService.NoAccountFoundException nafe) {
            log.error(nafe.getMessage(), nafe);
        }
    }

    /*
    public void run() throws IOException {
        System.out.println("press <enter> to submit order");
        //System.in.read();
        // create new order
        OrderId orderId = services.orders().nextId();
        submitOrder(orderId, "MSFT", Side.B, 1000.0, 102.1);

        System.out.println("press <enter> to modify order price");
        System.in.read();

        try {
            while (services.orders().find(orderId).orderAction() == OrderAction.WAIT) {
                log.info("Waiting on Ack of Order.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {

                }
            }
            modifyOrderPrice(orderId, 102.34);

            System.out.println("press <enter> to modify order qty");
            System.in.read();

            while (services.orders().find(orderId).orderAction() == OrderAction.WAIT) {
                log.info("Waiting on Ack of Order.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {

                }
            }

            modifyOrderQty(orderId, 900.0);

            System.out.println("press <enter> to cancel order");
            System.in.read();

            while (services.orders().find(orderId).orderAction() == OrderAction.WAIT) {
                log.info("Waiting on Ack of Order.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {

                }
            }

            cancelOrder(orderId);

            System.out.println("press <enter> to finish testing");
            System.in.read();

        } catch (OrderService.NoOrderFoundException nofe) {
            log.error(nofe.getMessage(), nofe);
        }
    }
    */

    public void submitOrder(OrderId orderId, String symbol, Side side, double qty, double px) {
        EquityOrder newOrder = EquityOrder.create(orderId, new Client(sessionID.getTargetCompID()))
                .account(testAccount)
                .instrument(services.products().find(InstrumentSource.RIC, symbol))
                .broker(Broker.DEFAULT)
                .execStrategy(ExecStrategy.DEFAULT)
                .orderType(OrderType.LIMIT)
                .side(side)
                .orderQty(qty)
                .limitPrice(px)
                .end();
        System.out.println("Submitting Order");
        System.out.println(newOrder.toString());
        log.info("[CLO] Submitting: " + newOrder);
        // submit to services (which sends it to broker)
        services.orders().submit(newOrder);
        log.info("[CLO] Submitted: " + newOrder);
    }

    private void modifyOrderPrice(OrderId orderId, double px) throws OrderService.NoOrderFoundException {
        // retrieve current version of order (after sent to broker)
        EquityOrder currentOrder = services.orders().find(orderId);
        modifyOrder(orderId, currentOrder.orderQty(), px);
    }

    private void modifyOrderQty(OrderId orderId, double qty) throws OrderService.NoOrderFoundException {
        // retrieve current version of order (after sent to broker)
        EquityOrder currentOrder = services.orders().find(orderId);
        modifyOrder(orderId, qty, currentOrder.limitPrice());
    }

    public void modifyOrder(OrderId orderId, double qty, double px) throws OrderService.NoOrderFoundException {
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
        log.info("[CLO] Replacing: " + currentOrder);

        // submit to services the modified order (which sends it to broker)
        services.orders().modify(modifiedOrder);
        log.info("[CLO] Replaced: " + modifiedOrder);
    }

    public void cancelOrder(OrderId orderId) throws OrderService.NoOrderFoundException {
        // retrieve current version of order
        EquityOrder currentOrder = services.orders().find(orderId);
        // cancel current version
        EquityOrder canOrder = currentOrder.modify().orderAction(OrderAction.CANCEL).end();
        System.out.println("Cancelling Order");
        System.out.println(canOrder.toString());
        log.info("[CLO] Canceling: " + currentOrder);
        // submit to services (which sends it to broker)
        services.orders().modify(canOrder);
        log.info("[CLO] Canceled: " + canOrder);
    }

    public void orderStatus(OrderId orderId) throws OrderService.NoOrderFoundException {
        EquityOrder currentOrder = services.orders().find(orderId);
        System.out.println("Order Status");
        System.out.println(currentOrder.toString());
        log.info("[CLO] Status: " + currentOrder);
    }

    public void orderHistory(OrderId orderId) throws OrderService.NoOrderFoundException {
        System.out.println("Order History");
        for (Order order : services.orders().getHistory(orderId)) {
            System.out.println(order.toString());
            log.info("[CLO] History: " + order);
        }
    }
}
