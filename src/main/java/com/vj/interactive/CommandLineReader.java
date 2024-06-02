package com.vj.interactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Reads a line of input from command line.
 *
 * This class runs on its own thread waiting for input from STDIN.
 * The input is accept(ed) by the CommandQueue.
 */
public class CommandLineReader extends Thread {

    private static final Logger log = LoggerFactory.getLogger(CommandLineReader.class);

    private boolean keepLooping = true;
    private CommandFactory commandFactory;
    private CommandQueue commandQueue;

    public CommandLineReader(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
        this.commandFactory = new CommandFactory();
    }

    @Override
    public void run() {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter command (new,modify,cancel,status,history,quit,help):");
        while (keepLooping) {
            try {
                // read line of input
                String commandInput = buffer.readLine();
                // convert into a command object
                Command command = commandFactory.make(commandInput);
                // add command object to command queue for processing
                commandQueue.accept(command);
                log.info("CLI: " + commandInput);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    log.error(ie.getMessage(), ie);
                }
            }
        }
    }

    public void shutdown() {
        keepLooping = false;
    }
}
