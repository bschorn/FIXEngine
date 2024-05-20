package com.vj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.management.ObjectName;

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


    public BuySide(SessionSettings settings) throws Exception {
        Application application = new Application(settings);
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory,
                messageFactory);

        jmxExporter = new JmxExporter();
        connectorObjectName = jmxExporter.register(initiator);
        log.info("Acceptor registered with JMX, name={}", connectorObjectName);
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
            }
        }
    }

    public void logout() {
        for (SessionID sessionId : initiator.getSessions()) {
            Session.lookupSession(sessionId).logout("user requested");
        }
    }


    public static void main(String[] args) throws Exception {
        try {
            if (args.length == 0) {
                args = new String[]{"src/main/resources/buyside.cfg"};
            }
            InputStream inputStream = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();

            BuySide buySide = new BuySide(settings);
            buySide.start();
            buySide.logon();

            System.out.println("press <enter> to quit");
            System.in.read();

            buySide.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static InputStream getSettingsInputStream(String[] args) throws FileNotFoundException {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = SellSide.class.getResourceAsStream("buyside.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("usage: " + SellSide.class.getName() + " [configFile].");
            System.exit(1);
        }
        return inputStream;
    }

}
