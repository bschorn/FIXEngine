package com.vj.publisher.buyside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.succession.message.OrderCancelRequestTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.fix42.OrderCancelRequest;

/**
 * BuySide - OrderCancelRequest
 *
 * EquityOrder (model) --transform--> OrderCancelRequest (quickfix) --send--> Broker
 */
public class OrderCancelRequestPublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelRequestPublisher.class);

    private final OrderCancelRequestTransform orderCancelRequestTransform;

    public OrderCancelRequestPublisher(OrderCancelRequestTransform orderCancelRequestTransform) {
        this.orderCancelRequestTransform = orderCancelRequestTransform;
    }

    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.CANCEL;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " sends a(n) " + OrderCancelRequest.class.getSimpleName() + " when EquityOrder.orderAction() == CANCEL";
    }


    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            OrderCancelRequest orderCancelRequest = orderCancelRequestTransform.outbound(equityOrder);
            send(orderCancelRequest, new Callback() {
                @Override
                public void onSuccess() throws OrderService.NoOrderFoundException {
                    services().orders().update(
                            equityOrder.update()
                                    .orderState(OrderState.CANCEL_SENT)
                                    .orderAction(OrderAction.NONE)
                                    .end());
                }

                @Override
                public void onException(Exception exception) {
                    //TODO handle exception
                    log.error(exception.getMessage());
                }
            });
        } catch (NoTransformationException nte) {
            log.error(nte.getMessage(), nte);
        }
    }
}
