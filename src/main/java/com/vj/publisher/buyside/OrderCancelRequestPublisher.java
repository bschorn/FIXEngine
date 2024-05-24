package com.vj.publisher.buyside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.transform.message.OrderCancelRequestTransform;
import quickfix.fix42.OrderCancelRequest;

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
            send(orderCancelRequest);
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
