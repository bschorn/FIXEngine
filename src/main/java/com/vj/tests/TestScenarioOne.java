package com.vj.tests;

import com.vj.model.attribute.*;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Market;
import com.vj.service.Services;
import quickfix.SessionID;

import java.io.IOException;

public class TestScenarioOne {


    Services services;
    SessionID sessionID;
    public TestScenarioOne(Services services, SessionID sessionID) {
        this.services = services;
        this.sessionID = sessionID;
    }

    public void run() throws IOException {
        System.out.println("press <enter> to submit order");
        System.in.read();
        EquityOrder newOrder1 = newOrder("MSFT", Side.B, 1000.0, 102.1);
        System.out.println(newOrder1.toString());
        services.orders().submit(newOrder1);
        System.out.println("press <enter> to modify order");
        EquityOrder currentOrder = services.orders().find(newOrder1.id());
        System.out.println(currentOrder.toString());
        EquityOrder modOrder1 = currentOrder.modify().orderAction(OrderAction.REPLACE).limitPrice(102.34).end();
        System.out.println(modOrder1.toString());
        services.orders().modify(modOrder1);
        System.out.println("press <enter> to cancel order");
        System.in.read();
        currentOrder = services.orders().find(modOrder1.id());
        EquityOrder canOrder1 = currentOrder.modify().orderAction(OrderAction.CANCEL).end();
        System.out.println(canOrder1.toString());
        services.orders().modify(canOrder1);

    }

    private EquityOrder newOrder(String symbol, Side side, double qty, double px) {
        return EquityOrder.create(services.orders().nextId(), new Client(sessionID.getTargetCompID()))
                .instrument(services.products().find(InstrumentSource.RIC, "MSFT"))
                .orderType(OrderType.LIMIT)
                .side(side)
                .orderQty(qty)
                .limitPrice(px)
                .end();
    }

}
