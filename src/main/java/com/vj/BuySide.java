package com.vj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.management.ObjectName;

import com.vj.interactive.CommandLineProcessor;
import com.vj.interactive.IOListener;
import com.vj.manager.SessionManager;
import com.vj.model.attribute.Account;
import com.vj.service.ClientService;
import com.vj.interactive.CommandLineSession;
import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.*;


/**
 * Entry point for the BuySide application.
 */
public class BuySide {

    private static final Logger log = LoggerFactory.getLogger(BuySide.class);
    private boolean initiatorStarted = false;
    private Initiator initiator = null;
    private final JmxExporter jmxExporter;
    private final ObjectName connectorObjectName;
    private final SessionSettings sessionSettings;
    private final ClientService clientService;

    public BuySide(SessionSettings settings, ClientService clientService, quickfix.Application listener) throws Exception {
        this.sessionSettings = settings;
        Application application = new Application(settings, false, listener);
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(
                settings.getBool("ScreenLogShowIncoming"),
                settings.getBool("ScreenLogShowOutgoing"),
                settings.getBool("ScreenLogEvents"));
        MessageFactory messageFactory = new DefaultMessageFactory();
        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory,
                messageFactory);

        jmxExporter = new JmxExporter();
        connectorObjectName = jmxExporter.register(initiator);
        log.info("Acceptor registered with JMX, name={}", connectorObjectName);
        this.clientService = clientService;
    }

    public void stop() {
        try {
            jmxExporter.getMBeanServer().unregisterMBean(connectorObjectName);
        } catch (Exception e) {
            log.error("Failed to unregister acceptor from JMX", e);
        }
        initiator.stop();
    }

    public void start() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        }
    }

    public void logon() {
        if (initiatorStarted) {
            for (SessionID sessionId : initiator.getSessions()) {
                Session.lookupSession(sessionId).logon();
                try {
                    String senderAccount = sessionSettings.getString(sessionId, "SenderAccount");
                    if (senderAccount == null) {
                        log.error(this.getClass().getSimpleName() + ".logon() - SenderAccount was not set for session: " + sessionId.getSenderCompID());
                    }
                    Account sessionAccount = new Account(senderAccount);
                    clientService.register(sessionId.getSenderCompID(), sessionAccount);
                    SessionManager.register(sessionId, sessionSettings.getSessionProperties(sessionId));
                } catch (ConfigError configError) {
                    log.error(configError.getMessage());
                }
            }
        }
    }

    public void logout() {
        for (SessionID sessionId : initiator.getSessions()) {
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    public SessionID sessionID() {
        for (SessionID sessionId : initiator.getSessions()) {
            return sessionId;
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        try {
            InputStream inputStream = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();
            BuySide buySide = null;
            boolean headless = Boolean.valueOf(System.getProperty("headless", "false"));
            if (!headless) {
                buySide = new BuySide(settings, Assembly.services().clients(), new IOListener());
                buySide.start();
                buySide.logon();
                CommandLineProcessor commandLineProcessor = new CommandLineProcessor(new CommandLineSession(Assembly.services(), buySide.sessionID()));
                commandLineProcessor.loop();
            } else {
                buySide = new BuySide(settings, Assembly.services().clients(), null);
                buySide.start();
                buySide.logon();
                System.out.println("press <enter> to quit");
                System.in.read();
            }
            buySide.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static InputStream getSettingsInputStream(String[] args) throws FileNotFoundException {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = SellSide.class.getResourceAsStream(System.getProperty("settings.resource"));
        } else if (args.length >= 1) {
            String resourceName = args[0];
            inputStream = SellSide.class.getResourceAsStream("/" + resourceName);
            if (inputStream == null) {
                System.out.println("Resource " + resourceName + " not found. Attempt to load as a file.");
                inputStream = new FileInputStream(resourceName);
            }
        }
        if (inputStream == null) {
            System.err.println("usage: " + BuySide.class.getName() + " [configFile].");
            System.exit(1);
        }
        return inputStream;
    }
}
