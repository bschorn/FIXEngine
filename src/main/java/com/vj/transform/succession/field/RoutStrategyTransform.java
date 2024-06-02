package com.vj.transform.succession.field;

import com.vj.manager.SessionManager;
import com.vj.model.attribute.Broker;
import com.vj.model.attribute.ExecStrategy;
import com.vj.transform.NoTransformationException;
import quickfix.field.ExDestination;
import quickfix.field.RoutStrategy;


/**
 * Sucesssion RoutStrategy to our ExecStrategy
 */
public class RoutStrategyTransform implements FieldTransform<RoutStrategy, ExecStrategy> {


      @Override
      public Class<RoutStrategy> fieldClass() {
            return RoutStrategy.class;
      }

      @Override
      public ExecStrategy inbound(RoutStrategy routStrategy) throws NoTransformationException {
            ExecStrategy execStrategy = ExecStrategy.from(routStrategy.getValue());
            if (execStrategy == null) {
                  throw new NoTransformationException(ExecStrategy.class, RoutStrategy.class, routStrategy.getValue());
            }
            return execStrategy;
      }

      @Override
      public RoutStrategy outbound(ExecStrategy execStrategy) throws NoTransformationException {
            if (execStrategy == ExecStrategy.DEFAULT) {
                  String routStrategy = SessionManager.getSessionProperty(SessionManager.getDefaultSessionID(), "RoutStrategy");
                  return new RoutStrategy(routStrategy);
            }
            return new RoutStrategy(execStrategy.asValue());
      }
}
