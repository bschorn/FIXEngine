package com.vj.publisher.buyside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.transform.message.OrderCancelReplaceRequestTransform;
import quickfix.fix42.OrderCancelReplaceRequest;


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
        try {
            send(orderCancelReplaceRequest);
            services().orders().update(
                    equityOrder.update()
                            .orderState(OrderState.REPLACE_SENT)
                            .orderAction(OrderAction.NONE)
                            .end());
        } catch (Exception ex) {
            //TODO handle exception
        }
    }
}
