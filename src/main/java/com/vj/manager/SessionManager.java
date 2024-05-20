package com.vj.manager;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

import java.util.Optional;

/**
 * Placeholder for the setting the target Session.
 *
 * BuySide only.
 */
public class SessionManager {

    private static SessionID DEFAULT_SESSION_ID = null;

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
}
