package com.vj.handler.order.sellside;

import com.vj.handler.MessageHandler;
import com.vj.model.attribute.ClientOrderId;
import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.service.OrderService;
import com.vj.transform.message.OrderCancelReplaceRequestTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix42.OrderCancelReplaceRequest;

public class OrderCancelReplaceRequestHandler implements MessageHandler<OrderCancelReplaceRequest> {

    private static final Logger log = LoggerFactory.getLogger(OrderCancelReplaceRequestHandler.class);

    private final OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform;

    public OrderCancelReplaceRequestHandler(OrderCancelReplaceRequestTransform orderCancelReplaceRequestTransform) {
        this.orderCancelReplaceRequestTransform = orderCancelReplaceRequestTransform;
    }

    @Override
    public String msgType() {
        return OrderCancelReplaceRequest.MSGTYPE;
    }

    /**
     * Sell-side
     */
    @Override
    public void handle(OrderCancelReplaceRequest message, SessionID sessionID) {
        try {
            double newOrderQty = message.getOrderQty().getValue();
            double newPrice = message.getPrice().getValue();
            EquityOrder equityOrder = orderCancelReplaceRequestTransform.inbound(message, sessionID);
            // check that newOrderQty isn't less than currently filled
            if (newOrderQty < equityOrder.totFillQty()) {
                // set newOrderQty to equal filledQty and close order
                newOrderQty = equityOrder.totFillQty();
                newPrice = equityOrder.limitPrice();
            }
            EquityOrder replacementOrder = EquityOrder.replicate(services().orders().nextId(), equityOrder.client(), new ClientOrderId(message.getClOrdID().getValue()))
                    .orderState(OrderState.OPEN)
                    .orderAction(OrderAction.ACCEPT_REPLACE)
                    .account(equityOrder.account())
                    .orderType(equityOrder.orderType())
                    .side(equityOrder.side())
                    .instrument(equityOrder.instrument())
                    .broker(equityOrder.broker())
                    .orderQty(newOrderQty)
                    .limitPrice(newPrice > 0 ? newPrice : equityOrder.limitPrice())
                    .end();
            // update/cancel original order
            services().orders().update(equityOrder.update()
                    .orderState(OrderState.CANCELED)
                    .end());
            // submit new order
            services().orders().submit(replacementOrder);
        } catch (FieldNotFound fnf) {
            // TODO: handle exception
            log.error(fnf.getMessage(), fnf);
        } catch (OrderService.NoOrderFoundException nofe) {
            // TODO: handle exception
            log.error(nofe.getMessage(), nofe);
        }

    }
}
