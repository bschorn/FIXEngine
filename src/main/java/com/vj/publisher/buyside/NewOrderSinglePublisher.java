package com.vj.publisher.buyside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.transform.message.NewOrderSingleTransform;
import quickfix.fix42.NewOrderSingle;

public class NewOrderSinglePublisher extends OrderPublisher<EquityOrder> {


    private final NewOrderSingleTransform newOrderSingleTransform;

    public NewOrderSinglePublisher(NewOrderSingleTransform newOrderSingleTransform) {
        this.newOrderSingleTransform = newOrderSingleTransform;
    }

    @Override
    public boolean test(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.OPEN;
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        NewOrderSingle newOrderSingle = newOrderSingleTransform.outbound(equityOrder);
        try {
            send(newOrderSingle);
            services().orders().update(
                    equityOrder.update()
                            .orderState(OrderState.OPEN_SENT)
                            .orderAction(OrderAction.NONE)
                            .end());
        } catch (Exception ex) {
            //TODO handle exception
        }
    }
}
