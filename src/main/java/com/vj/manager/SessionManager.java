package com.vj.manager;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

import java.util.*;

/**
 * Placeholder for the setting the target Session.
 *
 * BuySide only.
 */
public class SessionManager {

    private static SessionID DEFAULT_SESSION_ID = null;
    private static final Map<SessionID, Properties> SESSION_PROPS = new HashMap<>();
    private static final Properties EMPTY_PROPS = new Properties();

    public static void setDefaultSessionId(SessionID defaultSessionId) {
        DEFAULT_SESSION_ID = defaultSessionId;
    }
    public static SessionID getDefaultSessionID() {
        if (DEFAULT_SESSION_ID == null) {
            throw new RuntimeException("Default SessionID has not been set.");
        }
        return DEFAULT_SESSION_ID;
    }

    public static Optional<Exception> sendMessage(Message message) {
        try {
            Session session = Session.lookupSession(getDefaultSessionID());
            if (session == null) {
                throw new SessionNotFound(getDefaultSessionID().toString());
            }
            session.send(message);

        } catch (Exception ex) {
            return Optional.of(ex);
        }
        return Optional.empty();
    }

    public static void register(SessionID sessionId, Properties sessionProperties) {
        SESSION_PROPS.put(sessionId, sessionProperties);
    }

    public static String getSessionProperty(SessionID sessionId, String propertyKey) {
        return (String) SESSION_PROPS.getOrDefault(sessionId, EMPTY_PROPS).get(propertyKey);
    }
}
