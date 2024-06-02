package com.vj.interactive;

import com.vj.model.attribute.OrderId;
import com.vj.service.OrderService;

import java.util.stream.Collectors;

/**
 *
 */
public class CommandLineProcessor {

    private boolean keepLooping = true;
    private final CommandQueue commandQueue;
    private final CommandLineReader commandLineReader;
    private final CommandLineSession commandLineSession;

    public CommandLineProcessor(CommandLineSession commandLineSession) {
        this.commandLineSession = commandLineSession;
        this.commandQueue = new CommandQueue();
        this.commandLineReader = new CommandLineReader(this.commandQueue);
    }

    public void loop() {
        this.commandLineReader.start();
        while (keepLooping) {
            Command nextCommand = commandQueue.get();
            switch (nextCommand.commandType()) {
                case NEW:
                    Command.NewOrderCommand newOrder = nextCommand.get();
                    handle(newOrder);
                    break;
                case MODIFY:
                    Command.ModifyOrderCommand modifyOrder = nextCommand.get();
                    try {
                        handle(modifyOrder);
                    } catch (OrderService.NoOrderFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case CANCEL:
                    Command.CancelOrderCommand cancelOrder = nextCommand.get();
                    try {
                        handle(cancelOrder);
                    } catch (OrderService.NoOrderFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case STATUS:
                    Command.OrderStatusCommand orderStatus = nextCommand.get();
                    try {
                        handle(orderStatus);
                    } catch (OrderService.NoOrderFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case HISTORY:
                    Command.OrderHistoryCommand orderHistory = nextCommand.get();
                    try {
                        handle(orderHistory);
                    } catch (OrderService.NoOrderFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case SLEEP:
                    Command.SleepCommand sleepCommand = nextCommand.get();
                    try {
                        Thread.sleep(sleepCommand.duration.toMillis());
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    break;
                case HELP:
                    Command.HelpCommand helpCommand = nextCommand.get();
                    handle(helpCommand);
                    break;
                case QUIT:
                    quit();
                    break;
            }
        }
    }

    public void quit() {
        this.commandQueue.shutdown();
        this.commandLineReader.shutdown();
        this.keepLooping = false;
    }

    private void handle(Command.NewOrderCommand command) {
        commandLineSession.submitOrder(new OrderId(command.orderId), command.symbol, command.side, command.qty, command.price);
    }
    private void handle(Command.ModifyOrderCommand command) throws OrderService.NoOrderFoundException {
        commandLineSession.modifyOrder(new OrderId(command.orderId), command.qty, command.price);
    }
    private void handle(Command.CancelOrderCommand command) throws OrderService.NoOrderFoundException {
        commandLineSession.cancelOrder(new OrderId(command.orderId));
    }
    private void handle(Command.OrderStatusCommand command) throws OrderService.NoOrderFoundException {
        commandLineSession.orderStatus(new OrderId(command.orderId));
    }
    private void handle(Command.OrderHistoryCommand command) throws OrderService.NoOrderFoundException {
        commandLineSession.orderHistory(new OrderId(command.orderId));
    }
    private void handle(Command.HelpCommand command) {
        String helpMessage = command.commandTypeToHelp.name().toLowerCase() + ": " +
                command.commandTypeToHelp.getFields().stream()
                        .map(ct -> ct.name())
                        .collect(Collectors.joining(" "));
        System.out.println(helpMessage);
    }
}
