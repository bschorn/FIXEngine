package com.vj.transform.succession.field;

import com.vj.model.attribute.Exchange;
import com.vj.transform.NoTransformationException;
import quickfix.field.ExDestination;
import java.util.HashMap;
import java.util.Map;


/**
 * Sucesssion ExDestination to our Exchange
 */
public class ExDestinationTransform implements FieldTransform<ExDestination, Exchange> {

      private enum TransformState {
            NYSE(ExDestination.NYSE, Exchange.NYSE),
            NASDAQ(ExDestination.NASDAQ, Exchange.NASDAQ),
            BATS_Z(ExDestination.CBOE_BATS_BZX, Exchange.ANY_US_EQUITY),
            BATS_Y(ExDestination.CBOE_BATS_BYX, Exchange.ANY_US_EQUITY),
            EDGE_A(ExDestination.CBOE_EDGA, Exchange.ANY_US_EQUITY),
            EDGE_X(ExDestination.CBOE_EDGX, Exchange.ANY_US_EQUITY),
            IEX(ExDestination.INVESTORS_EXCHANGE, Exchange.ANY_US_EQUITY),
            MEMX(ExDestination.MEMBERS_EXCHANGE, Exchange.ANY_US_EQUITY),
            BOSTON(ExDestination.NASDAQ_OMX_BX, Exchange.ANY_US_EQUITY),
            PHILLY(ExDestination.NASDAQ_OMX_PSX, Exchange.ANY_US_EQUITY),
            ARCA(ExDestination.NYSE_ARCA, Exchange.ANY_US_EQUITY),
            AMEX(ExDestination.NYSE_AM_EQ, Exchange.ANY_US_EQUITY)
            // TODO there are more exchanges
            ;

            String exDestination;
            Exchange exchange;
            TransformState(String exDestination, Exchange exchange) {
                  this.exDestination = exDestination;
                  this.exchange = exchange;
            }
      }
      private final Map<String,Exchange> mapInbound;
      private final Map<Exchange,ExDestination> mapOutbound;

      public ExDestinationTransform() {
            mapInbound = new HashMap<>();
            mapOutbound = new HashMap<>();
            for (TransformState transformState : TransformState.values()) {
                  mapInbound.put(transformState.exDestination, transformState.exchange);
                  mapOutbound.put(transformState.exchange, new ExDestination(transformState.exDestination));
            }
      }

      @Override
      public Exchange inbound(ExDestination exDestination) throws NoTransformationException {
            Exchange exchange = mapInbound.get(exDestination.getValue());
            if (exchange == null) {
                  throw new NoTransformationException(Exchange.class, ExDestination.class, exDestination.getValue());
            }
            return exchange;
      }

      @Override
      public ExDestination outbound(Exchange exchange) throws NoTransformationException {
            ExDestination exDestination = mapOutbound.get(exchange);
            if (exDestination == null) {
                  throw new NoTransformationException(ExDestination.class, Exchange.class, exchange.toString());
            }
            return exDestination;
      }


}
