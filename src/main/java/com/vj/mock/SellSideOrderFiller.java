package com.vj.mock;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.model.entity.Order;
import com.vj.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SellSideOrderFiller extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SellSideOrderFiller.class);

    private boolean keepLooping = true;

    private final OrderService orderService;

    public SellSideOrderFiller(OrderService orderService) {
        this.orderService = orderService;
    }

    public void run() {
        while (keepLooping) {
            List<Order> openOrders = orderService.getOpenOrders();
            if (openOrders.isEmpty()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {

                }
                continue;
            }
            for (Order order : openOrders) {
                EquityOrder equityOrder = (EquityOrder) order;
                double lastPx = equityOrder.limitPrice();
                double lastQty = Math.min(equityOrder.unfilledQty(),10);
                double totalQty = equityOrder.totFillQty() + lastQty;
                double avgPx = lastPx;
                OrderState orderState = OrderState.PARTIAL;
                if (totalQty == equityOrder.orderQty()) {
                    orderState = OrderState.FILLED;
                }
                EquityOrder tradedOrder = equityOrder.update()
                        .orderAction(OrderAction.TRADED)
                        .orderState(orderState)
                        .lastFillPrice(lastPx)
                        .lastFillQty(lastQty)
                        .totalFillQty(totalQty)
                        .avgFillPrice(avgPx)
                        .end();
                try {
                    orderService.modify(tradedOrder);
                    Thread.sleep(2000);
                } catch (OrderService.NoOrderFoundException e) {
                    log.error(e.getMessage(), e);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void shutdown() {
        keepLooping = false;
        try {
            this.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
