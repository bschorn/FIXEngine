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

    /**
     * Is this the correct publisher for this EquityOrder?
     */
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
            // transform EquityOrder into OrderCancelRequest
            OrderCancelRequest orderCancelRequest = orderCancelRequestTransform.outbound(equityOrder);
            // create order event/version to add to OrderService upon success
            EquityOrder cancelOrder = equityOrder.update()
                    .orderState(OrderState.CANCEL_SENT)
                    .orderAction(OrderAction.WAIT)
                    .end();
            // send message to Broker
            send(orderCancelRequest, new Callback() {
                // upon successful send add new event/version to OrderService
                @Override
                public void onSuccess() throws OrderService.NoOrderFoundException {
                    services().orders().update(cancelOrder);
                }

                // handle exception
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
