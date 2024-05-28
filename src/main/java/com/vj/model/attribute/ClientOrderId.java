package com.vj.model.attribute;

public class ClientOrderId extends StringAttribute {
    public ClientOrderId(String value) {
        super(value);
    }
    public ClientOrderId(OrderId orderId, OrderVersion orderVersion) {
        super(orderId.toString() + "-" + orderVersion.toString());
    }

    public OrderId orderId() {
        return new OrderId(Long.valueOf(asValue().split("-")[0]));
    }

}
