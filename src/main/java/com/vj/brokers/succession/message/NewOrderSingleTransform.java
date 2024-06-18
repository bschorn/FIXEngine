package com.vj.brokers.succession.message;

import com.vj.brokers.succession.field.RoutStrategyTransform;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.field.ExDestinationTransform;
import com.vj.transform.field.OrdTypeTransform;
import com.vj.transform.field.SideTransform;
import quickfix.field.Account;
import quickfix.field.Side;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

public class NewOrderSingleTransform extends com.vj.transform.message.NewOrderSingleTransform {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;
    private final ExDestinationTransform exDestinationTransform;
    private final RoutStrategyTransform routStrategyTransform;

    public NewOrderSingleTransform(Services services, Transformers transformers) {
        super(services,transformers);
        this.services = services;
        this.ordTypeTransform = transformers.field(OrdType.class);
        this.sideTransform = transformers.field(Side.class);
        this.exDestinationTransform = transformers.field(ExDestination.class);
        this.routStrategyTransform = transformers.field(RoutStrategy.class);
    }

    @Override
    public Class<NewOrderSingle> messageClass() {
        return NewOrderSingle.class;
    }


    /**
     * Buy-Side
     */
    @Override
    public NewOrderSingle outbound(EquityOrder equityOrder) throws NoTransformationException {

        NewOrderSingle message = new NewOrderSingle();
        // required
        message.set(new Account(equityOrder.account().asValue()));
        message.set(new ClOrdID(equityOrder.clientOrderId().asValue()));
        message.set(new Rule80A(Rule80A.Day));
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        message.set(exDestinationTransform.outbound(equityOrder.broker()));
        message.set(routStrategyTransform.outbound(equityOrder.execStrategy()));
        message.set(new TimeInForce(TimeInForce.DAY));
        return message;
    }
}
