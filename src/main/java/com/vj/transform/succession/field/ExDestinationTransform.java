package com.vj.transform.succession.field;

import com.vj.manager.SessionManager;
import com.vj.model.attribute.Broker;
import com.vj.transform.NoTransformationException;
import quickfix.field.ExDestination;


/**
 * Sucesssion ExDestination to our Broker
 */
public class ExDestinationTransform implements FieldTransform<ExDestination, Broker> {


      @Override
      public Class<ExDestination> fieldClass() {
            return ExDestination.class;
      }

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
            if (broker == Broker.DEFAULT) {
                  String exDestination = SessionManager.getSessionProperty(SessionManager.getDefaultSessionID(), "ExDestination");
                  return new ExDestination(exDestination);
            }

            return new ExDestination(broker.asValue());
      }
}
