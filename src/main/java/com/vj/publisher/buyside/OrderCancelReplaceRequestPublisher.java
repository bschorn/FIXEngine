package com.vj.publisher.buyside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.message.OrderCancelReplaceRequestTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.fix42.OrderCancelReplaceRequest;

/**
 * BuySide - OrderCancelReplaceRequest
 *
 * EquityOrder (model) --transform--> OrderCancelReplaceRequest (quickfix) --send--> Broker
 */
public class OrderCancelReplaceRequestPublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelReplaceRequestPublisher.class);

    private final OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform;

    public OrderCancelReplaceRequestPublisher(OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform) {
        this.orderCancelReplaceRequestTransform = orderCancelReplaceRequestTransform;
    }


    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.REPLACE;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " sends a(n) " + OrderCancelReplaceRequest.class.getSimpleName() + " when EquityOrder.orderAction() == REPLACE";
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            OrderCancelReplaceRequest orderCancelReplaceRequest = orderCancelReplaceRequestTransform.outbound(equityOrder);
            EquityOrder replaceOrder = equityOrder.update()
                    .orderState(OrderState.REPLACE_SENT)
                    .orderAction(OrderAction.WAIT)
                    .end();
            send(orderCancelReplaceRequest, new Callback() {
                @Override
                public void onSuccess() throws OrderService.NoOrderFoundException {
                    services().orders().update(replaceOrder);
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
