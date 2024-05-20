package com.vj;

import com.vj.handler.MessageHandlers;
import com.vj.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DataDictionaryProvider;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogUtil;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;
import quickfix.field.ApplVerID;

public class Application implements quickfix.Application {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private MessageHandlers messageHandlers;

    public Application(SessionSettings settings) throws ConfigError, FieldConvertError {
        messageHandlers = Assembly.handlers();
    }

    public void onCreate(SessionID sessionID) {
        Session.lookupSession(sessionID).getLog().onEvent("Session Created");
    }

    public void onLogon(SessionID sessionID) {
        // this needs work unless there will only be one session (one broker connection per instance)
        SessionManager.setDefaultSessionId(sessionID);
    }

    public void onLogout(SessionID sessionID) {
        // this needs work unless there will only be one session (one broker connection per instance)
        SessionManager.setDefaultSessionId(null);
    }

    public void toAdmin(Message message, SessionID sessionID) {
        //TODO
    }

    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        //TODO
    }

    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, RejectLogon {
        //TODO
    }

    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, UnsupportedMessageType {
        messageHandlers.find(message, sessionID).handle(message, sessionID);
    }

    private void sendMessage(SessionID sessionID, Message message) {
        try {
            Session session = Session.lookupSession(sessionID);
            if (session == null) {
                throw new SessionNotFound(sessionID.toString());
            }
            session.send(message);

            DataDictionaryProvider dataDictionaryProvider = session.getDataDictionaryProvider();
            if (dataDictionaryProvider != null) {
                try {
                    dataDictionaryProvider.getApplicationDataDictionary(getApplVerID(session)).validate(message, true);
                } catch (Exception e) {
                    LogUtil.logThrowable(sessionID, "Outgoing message failed validation: "
                            + e.getMessage(), e);
                    return;
                }
            }
            session.send(message);
        } catch (SessionNotFound e) {
            log.error(e.getMessage(), e);
        }
    }

    private ApplVerID getApplVerID(Session session) {
        return MessageUtils.toApplVerID(session.getSessionID().getBeginString());
    }


}
