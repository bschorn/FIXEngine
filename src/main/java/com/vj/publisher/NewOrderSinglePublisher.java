package com.vj.publisher;

import com.vj.manager.SessionManager;
import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.transform.entity.NewOrderSingleTransform;
import quickfix.fix44.NewOrderSingle;

import java.util.Optional;

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
        send(newOrderSingle, equityOrder);
    }
}
