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
    private SessionSettings settings;
    private MessageHandlers messageHandlers;
    private ClientService clientService;
    private boolean sellside;

    public Application(SessionSettings settings, boolean sellside) throws ConfigError, FieldConvertError {
        this.settings = settings;
        this.sellside = sellside;
        messageHandlers = Assembly.handlers();
        clientService = Assembly.services().clients();
    }

    public void onCreate(SessionID sessionID) {
        log.info("onCreate: " + sessionID.toString());
    }

    public void onLogon(SessionID sessionID) {
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
        log.info("toAdmin: " + sessionID.toString() + " " + message.toString());
    }

    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        log.info("toApp: " + sessionID.toString() + " " + message.toString());
    }

    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, RejectLogon {
        log.info("fromAdmin: " + sessionID.toString() + " " + message.toString());
    }

    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat,
            IncorrectTagValue, UnsupportedMessageType {
        log.info("fromApp: " + sessionID.toString() + " " + message.toString());
        try {
            messageHandlers.find(message, sessionID).handle(message, sessionID);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /* - save for use as an example
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
