package com.vj;

/*
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
*/
public class LogConfig {

    public LogConfig(Class<?> parentClass) {
        /*
        // creates pattern layout
        PatternLayout layout = new PatternLayout();
        //String conversionPattern = "%d %p %x - %m%n [%c %t]";
        String conversionPattern = "%d %p [%C.%L %t] - %m%n";
        layout.setConversionPattern(conversionPattern);

        // creates console appender
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(layout);
        consoleAppender.activateOptions();

        // creates file appender
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile("log/" + parentClass.getSimpleName() + ".log");
        fileAppender.setLayout(layout);
        fileAppender.activateOptions();

        // configures the root logger
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(consoleAppender);
        rootLogger.addAppender(fileAppender);

        // creates a custom logger and log messages
        Logger logger = Logger.getLogger(LogConfig.class);
        logger.info(this.getClass().getSimpleName() + " log configuration initialized.");
        */
    }


    /**
     * For testing...
     *
     * @param args
     */
    //public static void main(String[] args) {
        //new LogConfig(LogConfig.class);
    //}
}