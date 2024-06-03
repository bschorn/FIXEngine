package com.vj.model.entity;


import com.vj.model.attribute.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;


public interface Order<M,U> {

    OrderId id();
    Account account();
    Client client();
    ClientOrderId clientOrderId();
    BrokerOrderId brokerOrderId();
    ClientOrderId origClientOrderId();
    LocalDate tradeDate();
    Instrument instrument();
    Broker broker();
    ExecStrategy execStrategy();
    OrderType orderType();
    Side side();
    LocalTime createdTS();
    OrderVersion version();
    LocalTime modifiedTS();
    LocalTime updatedTS();
    double orderQty();
    double limitPrice();
    double latestFillQty();
    double latestFillPrice();
    double totFillQty();
    double avgFillPrice();
    double unfilledQty();
    OrderAction orderAction();
    OrderState orderState();
    String error();
    default boolean isOpen() {
        switch (orderState()) {
            case OPEN:
            case PARTIAL:
                return true;
            default:
                return false;
        }
    }


    /**
     * After an outbound message
     */
    Order modify(OrderAction orderAction);

    /**
     * After an inbound message received
     */
    Order update(OrderState orderState);

    /**
     * Modify is a request to modify and will create a new ClientOrderId
     *
     */
    M modify();

    /**
     * Update is a synchronization and will get ClientOrderId from source.
     *
     */
    U update();
}
