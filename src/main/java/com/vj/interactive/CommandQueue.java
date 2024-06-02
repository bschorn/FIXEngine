package com.vj.interactive;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Command Queue
 *
 * Provides (thread) isolation between command instruction and command execution.
 */
public class CommandQueue implements Consumer<Command>, Supplier<Command> {

    private Deque<Command> deque = new ArrayDeque<>();

    /**
     * This is executed in the CommandLineReader thread.
     */
    @Override
    public void accept(Command command) {
        if (deque != null) {
            if (!deque.offerLast(command)) {
                System.out.println("Too many commands in queue. Ignoring command: " + command.toString());
            }
        }
    }

    /**
     * This is executed in the thread that called CommandLineProcessor.loop().
     */
    @Override
    public Command get() {
        if (deque != null) {
            Command nextCommand = deque.pollFirst();
            return (nextCommand == null) ? Command.SleepCommand : nextCommand;
        }
        return Command.QuitCommand;
    }

    public void shutdown() {
        this.deque = null;
    }
}
