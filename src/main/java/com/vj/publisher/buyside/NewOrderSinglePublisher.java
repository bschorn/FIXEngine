package com.vj.publisher.buyside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.message.NewOrderSingleTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.fix42.NewOrderSingle;

/**
 * BuySide - NewOrderSingle
 *
 * EquityOrder (model) --transform--> NewOrderSingle (quickfix) --send--> Broker
 */
public class NewOrderSinglePublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(NewOrderSinglePublisher.class);

    private final NewOrderSingleTransform newOrderSingleTransform;

    public NewOrderSinglePublisher(NewOrderSingleTransform newOrderSingleTransform) {
        this.newOrderSingleTransform = newOrderSingleTransform;
    }

    /**
     * Is this the correct publisher for this EquityOrder?
     */
    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.OPEN;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " sends a(n) " + NewOrderSingle.class.getSimpleName() + " when EquityOrder.orderAction() == OPEN";
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            NewOrderSingle newOrderSingle = newOrderSingleTransform.outbound(equityOrder);
            EquityOrder sentOrder = equityOrder.update()
                    .orderState(OrderState.OPEN_SENT)
                    .orderAction(OrderAction.WAIT)
                    .end();
            // send quickfix Message to broker
            send(newOrderSingle, new Callback() {
                @Override
                public void onSuccess() throws OrderService.NoOrderFoundException {
                    // update order service with info that it's been sent
                    services().orders().update(sentOrder);
                }

                @Override
                public void onException(Exception exception) {
                    if (exception instanceof OrderService.NoOrderFoundException) {
                        log.error(exception.getMessage());
                    } else {
                        try {
                            services().orders().update(equityOrder.update()
                                    .orderState(OrderState.OPEN_ERR)
                                    .orderAction(OrderAction.WAIT)
                                    .error(exception.getMessage())
                                    .end());
                        } catch (OrderService.NoOrderFoundException nofe) {
                            log.error(exception.getMessage());
                        }
                    }
                }
            });
        } catch (NoTransformationException nte) {
            log.error(nte.getMessage(), nte);
        }
    }

}
