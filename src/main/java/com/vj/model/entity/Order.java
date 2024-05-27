package com.vj.model.entity;


import com.vj.model.attribute.*;

import java.time.Instant;
import java.time.LocalDate;


public interface Order {

    OrderId id();
    Account account();
    Client client();
    ClientOrderId clientOrderId();
    BrokerOrderId brokerOrderId();
    ClientOrderId origClientOrderId();
    LocalDate tradeDate();
    Instrument instrument();
    Broker broker();
    OrderType orderType();
    Side side();
    Instant createdTS();
    OrderVersion version();
    Instant modifiedTS();
    Instant updatedTS();
    double orderQty();
    double limitPrice();
    double filledQty();
    double filledPrice();
    double unfilledQty();
    OrderAction orderAction();
    OrderState orderState();


    /**
     * After an outbound message
     */
    Order modify(OrderAction orderAction);

    /**
     * After an inbound message received
     */
    Order update(OrderState orderState);
}
