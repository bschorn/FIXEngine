package com.vj.transform.attribute;

import com.vj.model.attribute.OrderState;
import quickfix.field.OrdStatus;

import java.util.HashMap;
import java.util.Map;

public class OrderStateTransform implements AttributeTransform<OrdStatus,OrderState> {

      private enum TransformState {
            NEW(OrdStatus.NEW, OrderState.OPEN),
            REJECTED(OrdStatus.REJECTED, OrderState.REJECTED),
            CANCELED(OrdStatus.CANCELED, OrderState.CANCELED),
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

      public OrderStateTransform() {
            mapInbound = new HashMap<>();
            mapOutbound = new HashMap<>();
            for (TransformState transformState : TransformState.values()) {
                  mapInbound.put(transformState.ordStatus, transformState.orderState);
                  mapOutbound.put(transformState.orderState, new OrdStatus(transformState.ordStatus));
            }
      }

      @Override
      public OrderState inbound(OrdStatus ordStatus) {
            return mapInbound.get(ordStatus.getValue());
      }

      @Override
      public OrdStatus outbound(OrderState orderState) {
            return mapOutbound.get(orderState);
      }


}
