package com.vj.handler;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandlers {

    private final Map<String, List<MessageHandler>> messageMap = new HashMap<>();

    public <T> MessageHandler<T> find(Message message, SessionID sessionID) {
        try {
            List<MessageHandler> list = messageMap.get(message.getHeader().getString(MsgType.FIELD));
            if (list == null) {
                throw new RuntimeException(MessageHandler.class.getSimpleName() + ".find() - there are no message handlers for " + message.getHeader().getString(MsgType.FIELD));
            }
            if (list.size() == 1) {
                return list.get(0);
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).test(message, sessionID)) {
                    return list.get(i);
                }
            }

        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void register(MessageHandler messageHandler) {
        List<MessageHandler> list = messageMap.getOrDefault(messageHandler.msgType(), new ArrayList<>());
        list.add(messageHandler);
        messageMap.put(messageHandler.msgType(), list);
    }

    public int size() {
        return messageMap.size();
    }
}
