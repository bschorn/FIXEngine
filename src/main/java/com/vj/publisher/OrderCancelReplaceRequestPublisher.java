package com.vj.publisher;

import com.vj.manager.SessionManager;
import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.entity.OrderCancelReplaceRequestTransform;
import quickfix.fix44.OrderCancelReplaceRequest;


public class OrderCancelReplaceRequestPublisher extends OrderPublisher<EquityOrder> {


    private final OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform;

    public OrderCancelReplaceRequestPublisher(OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform) {
        this.orderCancelReplaceRequestTransform = orderCancelReplaceRequestTransform;
    }


    @Override
    public boolean test(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.REPLACE;
    }


    @Override
    public void publish(EquityOrder equityOrder) {
        OrderCancelReplaceRequest orderCancelReplaceRequest = orderCancelReplaceRequestTransform.outbound(equityOrder);
        send(orderCancelReplaceRequest, equityOrder);
    }
}
