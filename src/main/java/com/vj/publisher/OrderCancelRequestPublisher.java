package com.vj.publisher;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.entity.OrderCancelRequestTransform;
import quickfix.fix44.OrderCancelRequest;

public class OrderCancelRequestPublisher extends OrderPublisher<EquityOrder> {

    private final OrderCancelRequestTransform orderCancelRequestTransform;

    public OrderCancelRequestPublisher(OrderCancelRequestTransform orderCancelRequestTransform) {
        this.orderCancelRequestTransform = orderCancelRequestTransform;
    }

    @Override
    public boolean test(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.CANCEL;
    }


    @Override
    public void publish(EquityOrder equityOrder) {
        OrderCancelRequest orderCancelRequest = orderCancelRequestTransform.outbound(equityOrder);
        try {
            send(orderCancelRequest, equityOrder);
            services().orders().update(
                    equityOrder.update()
                            .orderState(OrderState.CANCEL_SENT)
                            .orderAction(OrderAction.NONE)
                            .end());
        } catch (Exception ex) {
            //TODO handle exception
        }
    }
}
