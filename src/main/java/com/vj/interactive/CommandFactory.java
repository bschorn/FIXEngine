package com.vj.interactive;

public class CommandFactory {

    public Command make(String line) throws Exception {
        String[] tokens = line.split(":");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Unable to parse entry: " + line);
        }
        Command.CommandType commandType = Command.CommandType.parse(tokens[0]);
        switch (commandType) {
            case NEW:
                return new Command.NewOrderCommand(tokens[1]);
            case CANCEL:
                return new Command.CancelOrderCommand(tokens[1]);
            case MODIFY:
                return new Command.ModifyOrderCommand(tokens[1]);
            case STATUS:
                return new Command.OrderStatusCommand(tokens[1]);
            case HISTORY:
                return new Command.OrderHistoryCommand(tokens[1]);
            case QUIT:
                return Command.QuitCommand;
            default:
                throw new UnsupportedOperationException("Unimplemented Command: " + commandType.name());
        }

    }
}
