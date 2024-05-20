package com.vj.model.entity;

import com.vj.model.attribute.*;

import java.time.Instant;
import java.time.LocalDate;

public class EquityOrder implements Order {


    private final Data data;
    private EquityOrder(Data data) {
        this.data = data;
    }

    @Override
    public OrderId id() {
        return data.orderId;
    }

    @Override
    public OrderVersion version() {
        return data.version;
    }

    @Override
    public Client client() {
        return data.client;
    }

    @Override
    public ClientOrderId clientOrderId() {
        return data.clientOrderId;
    }

    @Override
    public BrokerOrderId brokerOrderId() {
        return data.brokerOrderId;
    }

    @Override
    public ClientOrderId origClientOrderId() {
        return data.origClientOrderId;
    }

    @Override
    public Instrument instrument() {
        return data.instrument;
    }

    @Override
    public Instant modifiedTS() {
        return data.modifiedTS;
    }

    @Override
    public Instant updatedTS() {
        return data.updatedTS;
    }

    @Override
    public Instant createdTS() {
        return data.createdTS;
    }

    @Override
    public LocalDate tradeDate() {
        return data.tradeDate;
    }

    @Override
    public double orderQty() {
        return data.filledQty;
    }

    @Override
    public double limitPrice() {
        return data.filledPrice;
    }

    @Override
    public double filledQty() {
        return 0;
    }

    @Override
    public double filledPrice() {
        return 0;
    }

    @Override
    public double unfilledQty() {
        return data.filledQty - data.filledQty;
    }

    @Override
    public OrderAction orderAction() {
        return data.orderAction;
    }

    @Override
    public Side side() {
        return data.side;
    }

    @Override
    public OrderType orderType() {
        return data.orderType;
    }

    @Override
    public OrderState orderState() {
        return data.orderState;
    }

    @Override
    public Order modify(OrderAction orderAction) {
        return this.modify().orderAction(orderAction).end();
    }

    @Override
    public Order update(OrderState orderState) {
        return this.update().orderState(orderState).orderAction(OrderAction.NONE).end();
    }


    @Override
    public String toString() {
        return new StringBuilder()
                   .append(data.orderId)
                   .append("v")
                   .append(data.version)
                   .append(" [")
                   .append("] ")
                   .append(data.orderState.name())
                   .append(" ")
                   .append(data.instrument.toString())
                   .append(" ")
                   .append(data.side)
                   .append(" ")
                   .append(data.filledQty)
                   .append("@")
                   .append(data.filledPrice)
                   .append(" ")
                   .append(data.modifiedTS)
                   .toString();
    }

    /**
     * Initial Order Creation
     */
    public static OrderCreator create(OrderId orderId, Client client) {
        return new OrderCreator(orderId, client);
    }

    /**
     * Modify Order for Publishing (outbound)
     */
    public OrderModifier modify() {
        return new OrderModifier(this);
    }

    /**
     * Update Order from Report (inbound)
     *
     */
    public OrderUpdater update() {
        return new OrderUpdater(this);
    }

    /**
     * Order Data Struct (record)
     */
    private static class Data {
        // Immutables
        OrderId orderId;
        Client client;
        Instrument instrument;
        LocalDate tradeDate;
        OrderType orderType;
        Side side;
        Instant createdTS;

        // Mutability from Replace
        ClientOrderId clientOrderId;
        BrokerOrderId brokerOrderId = null;
        ClientOrderId origClientOrderId;

        // Systematic mutation
        OrderVersion version;
        Instant modifiedTS;
        Instant updatedTS;

        // Modifyable
        double orderQty;
        double limitPrice;
        // Updatable
        double filledQty = 0.0;
        double filledPrice = 0.0;

        // Where are we now
        OrderState orderState;
        // What do we want to do now?
        OrderAction orderAction;
    }

    /**
     * Outbound
     *
     * Initial order creation
     */
    public static class OrderCreator {
        private Data data = new Data();
        public OrderCreator(OrderId orderId, Client client) {
            data.orderId = orderId;
            data.client = client;
            data.version = OrderVersion.initial();
            data.clientOrderId = new ClientOrderId(String.valueOf(orderId) + "-" + String.valueOf(data.version));
            data.origClientOrderId = data.clientOrderId;
            data.orderState = OrderState.CREATED;
            data.orderAction = OrderAction.OPEN;
        }
        public OrderCreator instrument(Instrument value) {
            data.instrument = value;
            return this;
        }
        public OrderCreator side(Side side) {
            data.side = side;
            return this;
        }
        public OrderCreator orderQty(double value) {
            data.orderQty = value;
            return this;
        }
        public OrderCreator limitPrice(double value) {
            data.limitPrice = value;
            return this;
        }
        public OrderCreator orderType(OrderType value) {
            data.orderType = value;
            return this;
        }
        public OrderCreator tradeDate(LocalDate value) {
            data.tradeDate = value;
            return this;
        }
        public EquityOrder end() {
            data.createdTS = Instant.now();
            data.modifiedTS = data.createdTS;
            return new EquityOrder(data);
        }
    }

    /**
     * Clone for the next Order event
     */
    private static abstract class OrderClone {
        private Data data = new Data();
        public OrderClone(EquityOrder equityOrder) {
            data.orderId = equityOrder.data.orderId;
            data.clientOrderId = equityOrder.data.clientOrderId;
            data.origClientOrderId = equityOrder.data.origClientOrderId;
            data.brokerOrderId = equityOrder.data.brokerOrderId;
            data.version = equityOrder.data.version.getNext();
            data.instrument = equityOrder.data.instrument;
            data.orderType = equityOrder.data.orderType;
            data.tradeDate = equityOrder.data.tradeDate;
            data.side = equityOrder.data.side;
            data.createdTS = equityOrder.data.createdTS;
            data.modifiedTS = equityOrder.data.modifiedTS;
            data.updatedTS = equityOrder.data.updatedTS;
            data.orderState = equityOrder.data.orderState;
            data.orderAction = equityOrder.data.orderAction;
            data.limitPrice = equityOrder.data.limitPrice;
            data.filledPrice = equityOrder.data.filledPrice;
            data.filledQty = equityOrder.data.filledQty;
        }
    }

    /**
     * Outbound
     *
     * A change initiated internally that needs to be published
     */
    public static class OrderModifier extends OrderClone {
        private Data data = new Data();
        public OrderModifier(EquityOrder equityOrder) {
            super(equityOrder);
        }
        public OrderModifier orderAction(OrderAction value) {
            data.orderAction = value;
            return this;
        }
        public OrderModifier orderQty(double value) {
            data.orderQty = value;
            return this;
        }
        public OrderModifier limitPrice(double value) {
            data.limitPrice = value;
            return this;
        }
        public EquityOrder end() {
            data.modifiedTS = Instant.now();
            return new EquityOrder(data);
        }
    }

    /**
     * Inbound
     *
     * A change that has occurred that was received from external source
     */
    public static class OrderUpdater extends OrderClone {
        private Data data = new Data();
        public OrderUpdater(EquityOrder equityOrder) {
            super(equityOrder);
        }
        public OrderUpdater orderState(OrderState value) {
            data.orderState = value;
            return this;
        }
        public OrderUpdater orderAction(OrderAction value) {
            data.orderAction = value;
            return this;
        }
        public OrderUpdater totalFillQty(double value) {
            data.filledQty = value;
            return this;
        }
        public OrderUpdater avgFillPrice(double value) {
            data.filledPrice = value;
            return this;
        }
        public EquityOrder end() {
            data.updatedTS = Instant.now();
            return new EquityOrder(data);
        }
    }
}
