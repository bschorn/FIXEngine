package com.vj.interactive;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CommandQueue implements Consumer<Command>, Supplier<Command> {

    private Deque<Command> deque = new ArrayDeque<>();

    @Override
    public void accept(Command command) {
        if (deque != null) {
            deque.addLast(command);
        }
    }

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
