package com.vj.handler.order.buyside;

import com.vj.transform.message.MessageTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrderDefaultHandler extends ExecutionReportHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderDefaultHandler.class);

    public OrderDefaultHandler(MessageTransform messageTransform) {
        super(messageTransform);
    }

    @Override
    public boolean isDefaultHandler() {
        return true;
    }


}
