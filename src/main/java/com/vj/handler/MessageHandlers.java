package com.vj.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of MessageHandler instances.
 * Providing a Message + SessionID the correct handler will be returned.
 * The SessionID is only utilized when client specific handler(s) are needed to support a customization.
 *
 */
public class MessageHandlers {

    private static final Logger log = LoggerFactory.getLogger(MessageHandlers.class);

    private final Map<String, List<MessageHandler>> messageMap = new HashMap<>();

    public <T> MessageHandler<T> find(Message message, SessionID sessionID) throws NoMessageHandlerException {
        try {
            List<MessageHandler> list = messageMap.get(message.getHeader().getString(MsgType.FIELD));
            if (list == null) {
                throw new RuntimeException(MessageHandler.class.getSimpleName() + ".find() - there are no message handlers for " + message.getHeader().getString(MsgType.FIELD));
            }
            MessageHandler messageHandler = null;
            if (list.size() == 1) {
                messageHandler = list.get(0);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isHandler(message, sessionID)) {
                        messageHandler = list.get(i);
                        break;
                    }
                }
            }
            if (messageHandler != null) {
                log.info(this.getClass().getSimpleName() + ".find() - " + messageHandler.getClass().getSimpleName() + " to handle: " + message);
                return messageHandler;
            }
            throw new NoMessageHandlerException("No message handler found for Message: " + message);
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }

    public void register(MessageHandler messageHandler) {
        log.info(this.getClass().getSimpleName() + ".register() - Register: MessageHandler[" + messageHandler + "]");
        List<MessageHandler> list = messageMap.getOrDefault(messageHandler.msgType(), new ArrayList<>());
        list.add(messageHandler);
        messageMap.put(messageHandler.msgType(), list);
    }

    public int size() {
        return messageMap.size();
    }

    public static class NoMessageHandlerException extends Exception {
        public NoMessageHandlerException(String message) {
            super(message);
        }
    }
}
