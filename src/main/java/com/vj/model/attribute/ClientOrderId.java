package com.vj.model.attribute;

public class ClientOrderId extends StringAttribute {
    private final OrderId orderId;
    public ClientOrderId(String value) {
        super(value);
        this.orderId = new OrderId(Long.valueOf(value.split("-")[0]));
    }
    public ClientOrderId(OrderId orderId, OrderVersion orderVersion) {
        super(orderId.toString() + "-" + orderVersion.toString());
        this.orderId = orderId;
    }

    public OrderId orderId() {
        return this.orderId;
    }

}
