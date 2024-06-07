package com.vj.transform.succession.field;

import com.vj.model.attribute.OrderState;
import com.vj.transform.NoTransformationException;
import quickfix.field.OrdStatus;

import java.util.HashMap;
import java.util.Map;

public class OrdStatusTransform implements FieldTransform<OrdStatus,OrderState> {

      private enum TransformState {
            NEW(OrdStatus.NEW, OrderState.OPEN),
            REJECTED(OrdStatus.REJECTED, OrderState.REJECTED),
            PENDING_CXL(OrdStatus.PENDING_CANCEL, OrderState.CANCEL_PEND),
            REPLACED(OrdStatus.REPLACED, OrderState.REPLACED),
            CANCELED(OrdStatus.CANCELED, OrderState.CANCELED),
            PENDING_REP(OrdStatus.PENDING_REPLACE, OrderState.REPLACE_PEND),
            PARTIAL(OrdStatus.PARTIALLY_FILLED, OrderState.PARTIAL),
            FILLED(OrdStatus.FILLED, OrderState.FILLED);
            char ordStatus;
            OrderState orderState;
            TransformState(char ordStatus, OrderState orderState) {
                  this.orderState = orderState;
                  this.ordStatus = ordStatus;
            }
      }
      private final Map<Character,OrderState> mapInbound;
      private final Map<OrderState,OrdStatus> mapOutbound;

      public OrdStatusTransform() {
            mapInbound = new HashMap<>();
            mapOutbound = new HashMap<>();
            for (TransformState transformState : TransformState.values()) {
                  mapInbound.put(transformState.ordStatus, transformState.orderState);
                  mapOutbound.put(transformState.orderState, new OrdStatus(transformState.ordStatus));
            }
      }

      @Override
      public Class<OrdStatus> fieldClass() {
            return OrdStatus.class;
      }

      @Override
      public OrderState inbound(OrdStatus ordStatus) throws NoTransformationException {
            OrderState orderState = mapInbound.get(ordStatus.getValue());
            if (orderState == null) {
                  throw new NoTransformationException(OrderState.class, OrdStatus.class, ordStatus.getValue());
            }
            return orderState;
      }

      @Override
      public OrdStatus outbound(OrderState orderState) throws NoTransformationException {
            OrdStatus ordStatus = mapOutbound.get(orderState);
            if (ordStatus == null) {
                  throw new NoTransformationException(OrdStatus.class, OrderState.class, orderState.toString());
            }
            return ordStatus;
      }


}
