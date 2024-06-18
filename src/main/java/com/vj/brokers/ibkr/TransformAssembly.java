package com.vj.brokers.ibkr;

import com.vj.brokers.ibkr.field.ExDestinationTransform;
import com.vj.brokers.ibkr.field.OrdStatusTransform;
import com.vj.brokers.ibkr.field.OrdTypeTransform;
import com.vj.brokers.ibkr.field.SideTransform;
import com.vj.brokers.ibkr.message.*;
import com.vj.service.Services;
import com.vj.transform.Transformers;

public class TransformAssembly {

    public static void assemble(Services services, Transformers transformers) {
        // transformers - fields
        transformers.register(new OrdStatusTransform());
        transformers.register(new OrdTypeTransform());
        transformers.register(new SideTransform());
        transformers.register(new ExDestinationTransform());
        // transformers - messages
        transformers.register(new NewOrderSingleTransform(services, transformers));
        transformers.register(new OrderCancelRequestTransform(services, transformers));
        transformers.register(new OrderCancelReplaceRequestTransform(services, transformers));
        transformers.register(new OrderCancelRejectTransform(services, transformers));
        transformers.register(new ExecutionReportTransform(services, transformers));
    }
}
