package com.vj.publisher.sellside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.transform.message.ExecutionReportTransform;
import quickfix.fix42.ExecutionReport;

public class ExecutionReportPublisher extends OrderPublisher<EquityOrder> {
    private final ExecutionReportTransform executionReportTransform;

    public ExecutionReportPublisher(ExecutionReportTransform executionReportTransform) {
        this.executionReportTransform = executionReportTransform;
    }

    @Override
    public boolean test(EquityOrder equityOrder) {
        return equityOrder.orderAction() != OrderAction.NONE;
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        ExecutionReport executionReport = executionReportTransform.outbound(equityOrder);
        switch (equityOrder.orderAction()) {
            case OPEN:
                break;
            case REPLACE:
                break;
            case CANCEL:
                break;
        }
        try {
            send(executionReport);
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
