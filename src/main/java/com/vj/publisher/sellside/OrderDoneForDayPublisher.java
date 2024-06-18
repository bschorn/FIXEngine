package com.vj.publisher.sellside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.service.OrderService;
import com.vj.transform.NoTransformationException;
import com.vj.transform.message.ExecutionReportTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.field.ExecType;
import quickfix.fix42.ExecutionReport;

/**
 * SellSide - ExecutionReport
 *
 * EquityOrder (model) --transform--> ExecutionReport (quickfix) --send--> Client
 */
public class OrderDoneForDayPublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(OrderDoneForDayPublisher.class);

    private final ExecutionReportTransform executionReportTransform;

    public OrderDoneForDayPublisher(ExecutionReportTransform executionReportTransform) {
        this.executionReportTransform = executionReportTransform;
    }

    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.DONE_DAY;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " sends a(n) " + ExecutionReport.class.getSimpleName() + " when EquityOrder.orderAction() == DONE_DAY";
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            ExecutionReport executionReport = executionReportTransform.outbound(equityOrder);
            executionReport.set(new ExecType(ExecType.DONE_FOR_DAY));
            send(executionReport, new Callback() {
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
