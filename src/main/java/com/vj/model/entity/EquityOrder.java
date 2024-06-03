package com.vj.model.entity;

import com.vj.model.attribute.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EquityOrder implements Order<EquityOrder.OrderModifier, EquityOrder.OrderUpdater> {
    private final Data data;

    private static DateTimeFormatter TIME_FMT = DateTimeFormatter.ISO_LOCAL_TIME;

    private EquityOrder(Data data) {
        this.data = data;
        if (data.orderAction == null) {
            throw new RuntimeException(OrderAction.class.getSimpleName() + " may not be null.");
        }
        if (data.orderState == null) {
            throw new RuntimeException(OrderState.class.getSimpleName() + " may not be null.");
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
    public ExecStrategy execStrategy() {
        return data.execStrategy;
    }

    @Override
    public LocalTime modifiedTS() {
        return data.modifiedTS;
    }

    @Override
    public LocalTime updatedTS() {
        return data.updatedTS;
    }

    @Override
    public LocalTime createdTS() {
        return data.createdTS;
    }

    @Override
    public LocalDate tradeDate() {
        return data.tradeDate;
    }

    @Override
    public double orderQty() {
        return data.orderQty;
    }

    @Override
    public double limitPrice() {
        return data.limitPrice;
    }

    @Override
    public double latestFillQty() {
        return data.latestFillQty;
    }

    @Override
    public double latestFillPrice() {
        return data.latestFillPrice;
    }

    @Override
    public double totFillQty() {
        return data.totFillQty;
    }

    @Override
    public double avgFillPrice() {
        return data.avgFillPrice;
    }

    @Override
    public double unfilledQty() {
        return data.totFillQty - data.totFillQty;
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
        return new StringBuilder()
                .append("OrderId[")
                .append(data.orderId)
                .append("] ver[")
                .append(data.version)
                .append("] ts[")
                //.append(TO_STRING_TS_FMT.format(data.createdTS))
                //.append("/")
                //.append(TO_STRING_TS_FMT.format(data.modifiedTS))
                //.append("/")
                .append(TIME_FMT.format(data.updatedTS != null ? data.updatedTS : data.modifiedTS != null ? data.modifiedTS : data.createdTS))
                .append("] id[")
                .append(data.orderId)
                .append("v")
                .append(data.version)
                .append("] clOrdId[")
                .append(data.clientOrderId)
                .append("/")
                .append(data.origClientOrderId)
                .append("]")
                .append(" acct[")
                .append(data.account)
                .append("] state[")
                .append(data.orderState.name())
                .append("] action[")
                .append(data.orderAction.name())
                .append("] strat[")
                .append(data.execStrategy)
                .append("] ord[")
                .append(data.instrument.toString())
                .append(" ")
                .append(data.side)
                .append(" ")
                .append(data.orderQty)
                .append("@")
                .append(data.limitPrice)
                .append("] trd[")
                .append(data.latestFillQty)
                .append("@")
                .append(data.latestFillPrice)
                .append("] tot[")
                .append(data.totFillQty)
                .append("@")
                .append(data.avgFillPrice)
                .append("]")
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
        ExecStrategy execStrategy;
        LocalDate tradeDate;
        OrderType orderType;
        Side side;
        LocalTime createdTS;

        // Mutability from Replace
        ClientOrderId clientOrderId;
        BrokerOrderId brokerOrderId = null;
        ClientOrderId origClientOrderId;

        // Systematic mutation
        OrderVersion version;
        LocalTime modifiedTS;
        LocalTime updatedTS;

        // Modifyable
        double orderQty;
        double limitPrice;
        // Updatable
        double latestFillQty = 0.0;
        double latestFillPrice = 0.0;
        // Updatable
        double totFillQty = 0.0;
        double avgFillPrice = 0.0;

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

        public OrderCreator execStrategy(ExecStrategy execStrategy) {
            data.execStrategy = execStrategy;
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
            data.createdTS = LocalTime.now();
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
            data.modifiedTS = LocalTime.now();
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

        public OrderUpdater lastFillQty(double value) {
            data.latestFillQty = value;
            return this;
        }

        public OrderUpdater lastFillPrice(double value) {
            data.latestFillPrice = value;
            return this;
        }

        public OrderUpdater totalFillQty(double value) {
            data.totFillQty = value;
            return this;
        }

        public OrderUpdater avgFillPrice(double value) {
            data.avgFillPrice = value;
            return this;
        }

        public OrderUpdater error(String error) {
            this.data.error = error;
            return this;
        }

        public EquityOrder end() {
            data.updatedTS = LocalTime.now();
            return new EquityOrder(data);
        }
    }
}
