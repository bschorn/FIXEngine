package com.vj;

import com.vj.handler.MessageHandlers;
import com.vj.manager.SessionManager;
import com.vj.model.attribute.Account;
import com.vj.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DataDictionaryProvider;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;

public class Application implements quickfix.Application {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private SessionSettings settings;
    private MessageHandlers messageHandlers;
    private ClientService clientService;
    private boolean sellside;
    private final quickfix.Application listener;

    public Application(SessionSettings settings, boolean sellside, quickfix.Application listener) throws ConfigError, FieldConvertError {
        this.settings = settings;
        this.sellside = sellside;
        this.listener = listener;
        messageHandlers = Assembly.handlers();
        clientService = Assembly.services().clients();
    }

    public void onCreate(SessionID sessionID) {
        if (listener != null) {
            listener.onCreate(sessionID);
        }
        log.info("onCreate: " + sessionID.toString());
    }

    public void onLogon(SessionID sessionID) {
        if (listener != null) {
            listener.onLogon(sessionID);
        }
        log.info("onLogon: " + sessionID.toString());
        // this needs work unless there will only be one session (one broker connection per instance)
        SessionManager.setDefaultSessionId(sessionID);
        if (sellside) {
            try {
                Account account = new Account(settings.getString(sessionID, "TargetAccount"));
                clientService.register(sessionID.getTargetCompID(), account);
            } catch (ConfigError e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void onLogout(SessionID sessionID) {
        // this needs work unless there will only be one session (one broker connection per instance)
        SessionManager.setDefaultSessionId(null);
    }

    public void toAdmin(Message message, SessionID sessionID) {
        if (listener != null) {
            listener.toAdmin(message, sessionID);
        }
        log.info("toAdmin: " + sessionID.toString() + " " + message.toString());
    }

    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        if (listener != null) {
            listener.toApp(message, sessionID);
        }
        log.info("toApp: " + sessionID.toString() + " " + message.toString());
    }

    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, RejectLogon {
        if (listener != null) {
            listener.fromAdmin(message, sessionID);
        }
        log.info("fromAdmin: " + sessionID.toString() + " " + message.toString());
    }

    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, UnsupportedMessageType {
        if (listener != null) {
            listener.fromApp(message, sessionID);
        }
        log.info("fromApp: " + sessionID.toString() + " " + message.toString());
        try {
            messageHandlers.find(message, sessionID).handle(message, sessionID);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /*
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
    */
}
