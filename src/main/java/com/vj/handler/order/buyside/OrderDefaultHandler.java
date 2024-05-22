package com.vj.handler.order.buyside;

import com.vj.transform.message.MessageTransform;


public class OrderDefaultHandler extends ExecutionReportHandler {


    public OrderDefaultHandler(MessageTransform messageTransform) {
        super(messageTransform);
    }

    @Override
    public boolean isDefaultHandler() {
        return true;
    }


}
