package com.vj.publisher.sellside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.succession.message.OrderCancelRejectTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.Text;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.OrderCancelReject;

/**
 * SellSide - ExecutionReport
 *
 * EquityOrder (model) --transform--> ExecutionReport (quickfix) --send--> Client
 */
public class OrderCancelRejectPublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelRejectPublisher.class);

    private final OrderCancelRejectTransform orderCancelRejectTransform;

    public OrderCancelRejectPublisher(OrderCancelRejectTransform orderCancelRejectTransform) {
        this.orderCancelRejectTransform = orderCancelRejectTransform;
    }

    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        switch (equityOrder.orderAction()) {
            case REJECT_REPLACE:
            case REJECT_CANCEL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " sends a(n) " + ExecutionReport.class.getSimpleName() + " when EquityOrder.orderAction() == REJECT_*";
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            OrderCancelReject orderCancelReject = orderCancelRejectTransform.outbound(equityOrder);
            orderCancelReject.set(new Text());
            orderCancelReject.set(new CxlRejReason('0'));
            orderCancelReject.set(new CxlRejResponseTo('1'));

            send(orderCancelReject, new Callback() {
                @Override
                public void onSuccess() throws OrderService.NoOrderFoundException {
                    services().orders().update(
                            equityOrder.update()
                                    .orderAction(OrderAction.NONE)
                                    .end());
                }

                @Override
                public void onException(Exception exception) {
                    //TODO handle exception
                    log.error(exception.getMessage(), exception);
                }
            });
        } catch (NoTransformationException nte) {
            log.error(nte.getMessage(), nte);
        }
    }

}
