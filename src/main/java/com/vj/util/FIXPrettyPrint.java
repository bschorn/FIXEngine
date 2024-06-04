package com.vj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.field.*;
import quickfix.fix42.Message;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelRequest;

import java.util.StringJoiner;

public class FIXPrettyPrint {

    private static final Logger log = LoggerFactory.getLogger(FIXPrettyPrint.class);

    private static final Field[] HEADER_FIELDS = new Field[]{
            new BeginString(),
            new BodyLength(),
            new MsgType(),
            new SenderCompID(),
            new TargetCompID(),
            new TargetSubID(),
            new OnBehalfOfCompID(),
            new OnBehalfOfSubID(),
            new DeliverToCompID(),
            new MsgSeqNum(),
            new SendingTime(),
            new OrigSendingTime(),
            new PossDupFlag(),
            new PossResend(),
            new LastMsgSeqNumProcessed()
    };
    private static final Field[] NEW_ORDER = new Field[]{
            new Account(),
            new ClearingAccount(),
            new ClOrdID(),
            new Symbol(),
            new SymbolSfx(),
            new SecurityID(),
            new SecurityIDSource(),
            new Side(),
            new OrderQty(),
            new OrdType(),
            new Price(),
            new StopPx(),
            new Rule80A(),
            new ExDestination(),
            new TimeInForce(),
            new ExecInst(),
            new MinQty(),
            new MaxFloor(),
            new LocateReqd(),
            new PegDifference(),
            new DiscretionOffsetValue(),
            new LocateBroker(),
            new ClientInfo(),
            new OrderTTL(),
            new WashTradePrevention(),
            new PostOnly(),
            new RoutStrategy()
    };
    private static final Field[] REPLACE_ORDER = new Field[]{
            new Account(),
            new ClearingAccount(),
            new ClOrdID(),
            new OrigClOrdID(),
            new OrderID(),
            new OrderQty(),
            new Price(),
            new StopPx(),
            new OrdType(),
            new Symbol(),
            new SymbolSfx(),
            new Side(),
            new LocateReqd(),
            new LocateBroker(),
            new ClientInfo(),
            new CancelOrigOnReject()
    };
    private static final Field[] CANCEL_ORDER = new Field[]{
            new Account(),
            new ClearingAccount(),
            new ClOrdID(),
            new OrigClOrdID(),
            new OrderID(),
            new Symbol(),
            new SymbolSfx(),
            new Side(),
            new MassActionType()
    };

    public static String format(Message message) {
        if (message instanceof NewOrderSingle) {
            return format((NewOrderSingle) message);
        }
        if (message instanceof OrderCancelReplaceRequest) {
            return format((OrderCancelReplaceRequest) message);
        }
        if (message instanceof OrderCancelRequest) {
            return format((OrderCancelRequest) message);
        }
        try {
            return "Unhandled Message Type: " + message.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound e) {
            return "";
        }
    }
    public static String format(NewOrderSingle message) {
        StringJoiner joiner = new StringJoiner("\n    ", "NewOrderSingle\n    ", "");
        return format(message, NEW_ORDER, joiner);
    }
    public static String format(OrderCancelReplaceRequest message) {
        StringJoiner joiner = new StringJoiner("\n    ","OrderCancelReplaceRequest\n    ","");
        return format(message, REPLACE_ORDER, joiner);
    }
    public static String format(OrderCancelRequest message) {
        StringJoiner joiner = new StringJoiner("\n    ","OrderCancelRequest\n    ","");
        return format(message, CANCEL_ORDER, joiner);
    }
    private static String format(Message message, Field[] fields, StringJoiner joiner) {
        for (Field field : fields) {
            if (message.isSetField(field.getTag())) {
                try {
                    joiner.add(field.getTag() + "-" + field.getClass().getSimpleName() + "=" + message.getString(field.getTag()));
                } catch (FieldNotFound e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                joiner.add(field.getTag() + "-" + field.getClass().getSimpleName() + "=<not set>" );
            }
        }
        return format((Message.Header) message.getHeader()) + "\n" + joiner;
    }
    private static String format(Message.Header header) {
        StringJoiner joiner = new StringJoiner("\n    ","HEADER\n    ","");
        for (Field field : HEADER_FIELDS) {
            if (header.isSetField(field.getTag())) {
                try {
                    joiner.add(field.getTag() + "-" + field.getClass().getSimpleName() + "=" + header.getString(field.getTag()));
                } catch (FieldNotFound e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                joiner.add(field.getTag() + "-" + field.getClass().getSimpleName() + "=<not set>" );
            }
        }
        return joiner.toString();
    }
}
