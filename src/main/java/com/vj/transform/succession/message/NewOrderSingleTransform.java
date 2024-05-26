package com.vj.transform.succession.message;

import com.vj.model.attribute.*;
import com.vj.model.entity.EquityOrder;
import com.vj.service.Services;
import com.vj.transform.NoTransformationException;
import com.vj.transform.Transformers;
import com.vj.transform.succession.field.ExDestinationTransform;
import com.vj.transform.succession.field.OrdTypeTransform;
import com.vj.transform.succession.field.SecurityIDSourceTransform;
import com.vj.transform.succession.field.SideTransform;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.*;
import quickfix.field.Account;
import quickfix.field.Side;
import quickfix.fix42.NewOrderSingle;

public class NewOrderSingleTransform implements MessageTransform<NewOrderSingle, EquityOrder> {

    private final Services services;
    private final OrdTypeTransform ordTypeTransform;
    private final SideTransform sideTransform;
    private final SecurityIDSourceTransform securityIDSourceTransform;
    private final ExDestinationTransform exDestinationTransform;

    public NewOrderSingleTransform(Services services, Transformers transformers) {
        this.services = services;
        this.ordTypeTransform = transformers.field(OrdType.class);
        this.sideTransform = transformers.field(Side.class);
        this.securityIDSourceTransform = transformers.field(SecurityIDSource.class);
        this.exDestinationTransform = transformers.field(ExDestination.class);
    }

    /**
     * Sell-Side
     */
    @Override
    public EquityOrder inbound(NewOrderSingle newOrderSingle, SessionID sessionID, Object... objects) throws FieldNotFound, NoTransformationException {
        // this is a kludge
        if (objects.length == 0 || !(objects[0] instanceof OrderId)) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".inbound(...) requires third paramater to be an OrderId.");
        }
        OrderId orderId = (OrderId) objects[0];
        // end of kludge

        Client client = services.clients().lookupClient(sessionID.getTargetCompID());
        return EquityOrder.create(orderId, client)
                .orderState(OrderState.OPEN)
                .orderAction(OrderAction.OPEN)
                .account(new com.vj.model.attribute.Account(newOrderSingle.getAccount().getValue()))
                .orderType(ordTypeTransform.inbound(newOrderSingle.getOrdType()))
                .side(sideTransform.inbound(newOrderSingle.getSide()))
                .instrument(services.products().find(securityIDSourceTransform.inbound(newOrderSingle.getSecurityIDSource()), newOrderSingle.getSecurityID().getValue()))
                .exchange(exDestinationTransform.inbound(newOrderSingle.getExDestination()))
                .orderQty(newOrderSingle.getOrderQty().getValue())
                .limitPrice(newOrderSingle.getPrice().getValue())
                .end();
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
        message.set(new Symbol(equityOrder.instrument().toString()));
        message.set(sideTransform.outbound(equityOrder.side()));
        message.set(new OrderQty(equityOrder.orderQty()));
        message.set(new Price(equityOrder.limitPrice()));
        message.set(ordTypeTransform.outbound(equityOrder.orderType()));
        message.set(exDestinationTransform.outbound(equityOrder.exchange())); // this probably shouldn't come from the order
        // optional
        message.set(new SecurityID(equityOrder.instrument().get(InstrumentSource.SEDOL)));
        message.set(securityIDSourceTransform.outbound(InstrumentSource.SEDOL));
        message.set(new TimeInForce(TimeInForce.DAY));
        return message;
    }
}