package com.vj.transform.succession.field;

import com.vj.model.attribute.Broker;
import com.vj.transform.NoTransformationException;
import quickfix.field.ExDestination;
import java.util.HashMap;
import java.util.Map;


/**
 * Sucesssion ExDestination to our Broker
 */
public class ExDestinationTransform implements FieldTransform<ExDestination, Broker> {

      @Override
      public Broker inbound(ExDestination exDestination) throws NoTransformationException {
            Broker broker = Broker.from(exDestination.getValue());
            if (broker == null) {
                  throw new NoTransformationException(Broker.class, ExDestination.class, exDestination.getValue());
            }
            return broker;
      }

      @Override
      public ExDestination outbound(Broker broker) throws NoTransformationException {
            return new ExDestination(broker.asValue());
      }
}
