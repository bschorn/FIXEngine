package com.vj.model.entity;

import com.vj.model.attribute.*;

import java.time.Instant;
import java.time.LocalDate;

public class EquityOrder implements Order<EquityOrder.OrderModifier, EquityOrder.OrderUpdater> {
    private final Data data;

    private EquityOrder(Data data) {
        this.data = data;
        if (data.orderAction == null) {
            throw new RuntimeException(OrderAction.class.getSimpleName() + " may not be null.");
        }
        if (data.orderState == null) {
            throw new RuntimeException(OrderState.class.getSimpleName() + " may not be null.");
        }
        if (data.broker == null) {
            throw new RuntimeException(Broker.class.getSimpleName() + " may not be null.");
        }
        if (data.origClientOrderId == null) {
            throw new RuntimeException("Orig" + ClientOrderId.class.getSimpleName() + " may not be null.");
        }
        if (data.client == null) {
            throw new RuntimeException(Client.class.getSimpleName() + " may not be null.");
        }
        if (data.clientOrderId == null) {
            throw new RuntimeException(ClientOrderId.class.getSimpleName() + " may not be null.");
        }
        if (data.orderId == null) {
            throw new RuntimeException(OrderId.class.getSimpleName() + " may not be null.");
        }
        if (data.account == null) {
            throw new RuntimeException(Account.class.getSimpleName() + " may not be null.");
        }
        if (data.orderType == null) {
            throw new RuntimeException(OrderType.class.getSimpleName() + " may not be null.");
        }
        if (data.side == null) {
            throw new RuntimeException(Side.class.getSimpleName() + " may not be null.");
        }
    }

    @Override
    public OrderId id() {
        return data.orderId;
    }

    @Override
    public Account account() {
        return data.account;
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
    public Broker broker() {
        return data.broker;
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
    public String error() {
        return data.error == null ? "" : data.error;
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
        return new StringBuilder("Id: ")
                .append(data.orderId)
                .append(" Ver")
                .append(data.version)
                .append(" Clid:")
                .append(data.clientOrderId)
                .append(" Bkid:")
                .append(data.brokerOrderId == null ? "unset" : data.brokerOrderId)
                .append(" ")
                .append(data.account)
                .append(" [")
                .append(data.orderState.name())
                .append(":")
                .append(data.orderAction.name())
                .append("] ")
                .append(data.broker)
                .append(" ")
                .append(data.side)
                .append(" ")
                .append(data.instrument.toString())
                .append(" ")
                .append(data.orderQty)
                .append("@")
                .append(data.limitPrice)
                .append(" Filled[")
                .append(data.filledQty)
                .append("@")
                .append(data.filledPrice)
                .append("] TS:")
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
     * Order Replication
     */
    public static OrderReplicator replicate(OrderId orderId, Client client, ClientOrderId clientOrderId) {
        return new OrderReplicator(orderId, client, clientOrderId);
    }

    /**
     * Modify Order for Publishing (outbound)
     */
    @Override
    public OrderModifier modify() {
        return new OrderModifier(this);
    }

    /**
     * Update Order from Report (inbound)
     */
    @Override
    public OrderUpdater update() {
        return new OrderUpdater(this);
    }

    /**
     * Order Data Struct (record)
     */
    private static class Data implements Cloneable {
        // Immutables
        OrderId orderId;
        Account account;
        Client client;
        Instrument instrument;
        Broker broker;
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

        String error = null;

        @Override
        public Object clone() {
            try {
                return (Data) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Outbound
     * <p>
     * Initial order creation
     */
    public static class OrderCreator {
        protected final Data data = new Data();

        public OrderCreator(OrderId orderId, Client client) {
            data.orderId = orderId;
            data.client = client;
            data.version = OrderVersion.initial();
            data.clientOrderId = new ClientOrderId(data.orderId, data.version);
            data.origClientOrderId = data.clientOrderId;
            data.orderState = OrderState.OPEN_REQ;
            data.orderAction = OrderAction.OPEN;
            data.tradeDate = LocalDate.now();
        }

        public OrderCreator account(Account account) {
            data.account = account;
            return this;
        }

        public OrderCreator orderState(OrderState orderState) {
            data.orderState = orderState;
            return this;
        }

        public OrderCreator orderAction(OrderAction orderAction) {
            data.orderAction = orderAction;
            return this;
        }

        public OrderCreator instrument(Instrument value) {
            data.instrument = value;
            return this;
        }

        public OrderCreator broker(Broker broker) {
            data.broker = broker;
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
     * Inbound
     * <p>
     * Order replication
     */
    public static class OrderReplicator extends OrderCreator {
        public OrderReplicator(OrderId orderId, Client client, ClientOrderId clientOrderId) {
            super(orderId, client);
            data.clientOrderId = clientOrderId;
            data.origClientOrderId = data.clientOrderId;
        }
    }

    /**
     * Clone for the next Order event
     */
    private static abstract class OrderClone {
        protected final Data data;

        public OrderClone(EquityOrder equityOrder) {
            data = (Data) equityOrder.data.clone();
            data.version = equityOrder.data.version.getNext();
            data.error = null;
        }
    }

    /**
     * Outbound
     * <p>
     * A change initiated internally that needs to be published
     */
    public static class OrderModifier extends OrderClone {
        public OrderModifier(EquityOrder equityOrder) {
            super(equityOrder);
        }

        public OrderModifier orderAction(OrderAction value) {
            data.orderAction = value;
            switch (data.orderAction) {
                case REPLACE:
                case CANCEL:
                    data.origClientOrderId = data.clientOrderId;
                    data.clientOrderId = new ClientOrderId(data.orderId, data.version);
                    break;
            }
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
     * <p>
     * A change that has occurred that was received from external source
     */
    public static class OrderUpdater extends OrderClone {
        public OrderUpdater(EquityOrder equityOrder) {
            super(equityOrder);
        }

        public OrderUpdater brokerOrderId(BrokerOrderId brokerOrderId) {
            data.brokerOrderId = brokerOrderId;
            return this;
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

        public OrderUpdater error(String error) {
            this.data.error = error;
            return this;
        }

        public EquityOrder end() {
            data.updatedTS = Instant.now();
            return new EquityOrder(data);
        }
    }
}
