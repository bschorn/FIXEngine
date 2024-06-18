package com.vj.brokers.succession;

import com.vj.brokers.succession.field.*;
import com.vj.brokers.succession.message.*;
import com.vj.service.Services;
import com.vj.transform.Transformers;

public class TransformAssembly {

    public static void assemble(Services services, Transformers transformers) {
        // transformers - fields
        transformers.register(new OrdStatusTransform());
        transformers.register(new OrdTypeTransform());
        transformers.register(new SideTransform());
        transformers.register(new ExDestinationTransform());
        transformers.register(new RoutStrategyTransform());
        // transformers - messages
        transformers.register(new NewOrderSingleTransform(services, transformers));
        transformers.register(new OrderCancelRequestTransform(services, transformers));
        transformers.register(new OrderCancelReplaceRequestTransform(services, transformers));
        transformers.register(new OrderCancelRejectTransform(services, transformers));
        transformers.register(new ExecutionReportTransform(services, transformers));
    }
}
