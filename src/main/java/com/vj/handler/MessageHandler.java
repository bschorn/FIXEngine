package com.vj.handler;

import com.vj.Assembly;
import com.vj.service.Services;
import quickfix.SessionID;

public interface MessageHandler<T> {
    String msgType();
    void handle(T t, SessionID sessionID);

    /**
     * The test(...) method determines if this is the appropriate handler for the Message and SessionID combination
     * presented.
     *
     * By default there will only be one handler per message but when there is more than one, implement the test(...) method to examine message to determine if it is yours.
     *
     */
    default boolean test(T t, SessionID sessionID) {
        return true;
    }

    /**
     * This flag indicates whether this handler is is the default handler for this msgType().
     *
     * If returns true, then this handler will only be used when no other handlers test() true.
     *
     */
    default boolean isDefaultHandler() {
        return false;
    }

    default Services services() {
        return Assembly.services();
    }
}
