package com.vj.publisher.sellside;

import com.vj.model.attribute.OrderAction;
import com.vj.model.attribute.OrderState;
import com.vj.model.entity.EquityOrder;
import com.vj.publisher.OrderPublisher;
import com.vj.transform.NoTransformationException;
import com.vj.transform.succession.message.ExecutionReportTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.fix42.ExecutionReport;

/**
 * SellSide - ExecutionReport
 *
 * EquityOrder (model) --transform--> ExecutionReport (quickfix) --send--> Client
 */
public class ExecutionReportPublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(ExecutionReportPublisher.class);

    private final ExecutionReportTransform executionReportTransform;

    public ExecutionReportPublisher(ExecutionReportTransform executionReportTransform) {
        this.executionReportTransform = executionReportTransform;
    }

    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        return equityOrder.orderAction() != OrderAction.NONE;
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            ExecutionReport executionReport = executionReportTransform.outbound(equityOrder);
            send(executionReport, new Callback() {
                @Override
                public void onSuccess() {
                    services().orders().update(
                            equityOrder.update()
                                    .orderState(OrderState.OPEN_SENT)
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
