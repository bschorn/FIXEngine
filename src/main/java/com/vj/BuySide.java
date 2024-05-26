package com.vj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import javax.management.ObjectName;

import com.vj.model.attribute.Account;
import com.vj.service.ClientService;
import com.vj.tests.TestScenarioOne;
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


    public BuySide(SessionSettings settings, ClientService clientService) throws Exception {
        this.sessionSettings = settings;
        Application application = new Application(settings);
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
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
                    Account sessionAccount = new Account(sessionSettings.getString(sessionId, "SenderAccount"));
                    clientService.register(sessionId.getSenderCompID(), sessionAccount);
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
            new LogConfig(BuySide.class);
            InputStream inputStream = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();

            BuySide buySide = new BuySide(settings, Assembly.services().clients());
            buySide.start();
            buySide.logon();

            boolean testing = Boolean.valueOf(System.getProperty("test", "false"));
            if (testing) {
                TestScenarioOne tester = new TestScenarioOne(
                        Assembly.services(),
                        buySide.sessionID(),
                        Account.getAccount());
                tester.run();
            } else {
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
