package com.vj.interactive;

public class CommandFactory {

    public Command make(String line) throws Exception {
        String[] tokens = line.split(":");
        Command.CommandType commandType = Command.CommandType.parse(tokens[0]);
        if (commandType == Command.CommandType.QUIT) {
            return Command.QuitCommand;
        }
        String params = "";
        if (tokens.length != 2) {
            if (commandType != Command.CommandType.HELP) {
                throw new UnsupportedOperationException("Unable to execute: " + line);
            }
        } else {
            params = tokens[1].trim();
        }

        switch (commandType) {
            case NEW:
                return new Command.NewOrderCommand(params);
            case CANCEL:
                return new Command.CancelOrderCommand(params);
            case MODIFY:
                return new Command.ModifyOrderCommand(params);
            case STATUS:
                return new Command.OrderStatusCommand(params);
            case HISTORY:
                return new Command.OrderHistoryCommand(params);
            case HELP:
                return new Command.HelpCommand(params);
            case QUIT:
                return Command.QuitCommand;
            default:
                throw new UnsupportedOperationException("Unimplemented Command: " + commandType.name());
        }

    }
}
