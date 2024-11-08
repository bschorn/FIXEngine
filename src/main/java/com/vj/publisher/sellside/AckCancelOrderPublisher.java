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
public class AckCancelOrderPublisher extends OrderPublisher<EquityOrder> {

    private static final Logger log = LoggerFactory.getLogger(AckCancelOrderPublisher.class);

    private final ExecutionReportTransform executionReportTransform;

    public AckCancelOrderPublisher(ExecutionReportTransform executionReportTransform) {
        this.executionReportTransform = executionReportTransform;
    }

    @Override
    public boolean isPublisher(EquityOrder equityOrder) {
        return equityOrder.orderAction() == OrderAction.ACK_CANCEL;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " sends a(n) " + ExecutionReport.class.getSimpleName() + " when EquityOrder.orderAction() == ACK_CANCEL";
    }

    @Override
    public void publish(EquityOrder equityOrder) {
        try {
            ExecutionReport executionReport = executionReportTransform.outbound(equityOrder);
            executionReport.set(new ExecType(ExecType.PENDING_CANCEL));
            send(executionReport, new Callback() {
                @Override
                public void onSuccess() throws OrderService.NoOrderFoundException {
                    services().orders().update(
                            equityOrder.update()
                                    .orderAction(OrderAction.ACCEPT_CANCEL)
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
