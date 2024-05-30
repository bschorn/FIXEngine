package com.vj.interactive;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandLineReader extends Thread {

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
        while (keepLooping) {
            try {
                System.out.println("Enter command (new,modify,cancel,status,history,quit,help):");
                commandQueue.accept(commandFactory.make(buffer.readLine()));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void shutdown() {
        keepLooping = false;
    }
}
