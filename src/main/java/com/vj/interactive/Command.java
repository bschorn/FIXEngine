package com.vj.interactive;

import com.vj.model.attribute.Side;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public interface Command {

    enum Field {
        Command,
        Symbol,
        Side,
        Qty,
        Price,
        OrderId;
    }
    enum CommandType {
        NEW(Field.Symbol, Field.Side, Field.Qty, Field.Price),
        CANCEL(Field.OrderId),
        MODIFY(Field.OrderId, Field.Qty, Field.Price),
        STATUS(Field.OrderId),
        HISTORY(Field.OrderId),
        SLEEP(),
        HELP(Field.Command),
        QUIT();

        private Field[] fields;
        CommandType(Field...fields) {
            this.fields = fields;
        }

        public List<Field> getFields() {
            return Arrays.asList(fields);
        }

        public int getIndex(Field field) {
            for (int i = 0; i < 0; i++) {
                if (fields[i] == field) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Field: " + field.name() + " is not part of the command: " + name());
        }

        public static CommandType parse(String value) throws Exception {
            for (CommandType commandType : CommandType.values()) {
                if (commandType.name().equalsIgnoreCase(value)) {
                    return commandType;
                }
            }
            throw new Exception("Command Type unknown: " + value);
        }
    }

    long id();
    default <T> T get() { return (T) this; }
    CommandType commandType();

    SleepCommand SleepCommand = new SleepCommand(Duration.ofMillis(1000));
    QuitCommand QuitCommand = new QuitCommand();

    abstract class AbstractCommand implements Command {
        private static final AtomicLong nextId = new AtomicLong(0);
        private final long id = nextId.getAndIncrement();
        @Override
        public long id() {
            return this.id;
        }
    }

    class NewOrderCommand extends AbstractCommand {
        public final String symbol;
        public final Side side;
        public final double qty;
        public final double price;
        public NewOrderCommand(String line) {
            String[] tokens = line.split("[ |@]*");
            this.symbol = tokens[commandType().getIndex(Field.Symbol)];
            this.side = Side.from(tokens[commandType().getIndex(Field.Side)]);
            this.qty = Double.parseDouble(tokens[commandType().getIndex(Field.Qty)]);
            this.price = Double.parseDouble(tokens[commandType().getIndex(Field.Price)]);
        }

        @Override
        public CommandType commandType() {
            return CommandType.NEW;
        }
    }
    class CancelOrderCommand extends AbstractCommand {
        public final String orderId;
        public CancelOrderCommand(String line) {
            String[] tokens = line.split("[ |@]*");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
        }
        @Override
        public CommandType commandType() {
            return CommandType.CANCEL;
        }
    }
    class ModifyOrderCommand extends AbstractCommand {
        public final String orderId;
        public final double qty;
        public final double price;
        public ModifyOrderCommand(String line) {
            String[] tokens = line.split("[ |@]*");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
            this.qty = Double.parseDouble(tokens[commandType().getIndex(Field.Qty)]);
            this.price = Double.parseDouble(tokens[commandType().getIndex(Field.Price)]);
        }
        @Override
        public CommandType commandType() {
            return CommandType.MODIFY;
        }
    }
    class OrderStatusCommand extends AbstractCommand {
        public final String orderId;
        public OrderStatusCommand(String line) {
            String[] tokens = line.split("[ |@]*");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
        }
        @Override
        public CommandType commandType() {
            return CommandType.STATUS;
        }
    }
    class OrderHistoryCommand extends AbstractCommand {
        public final String orderId;
        public OrderHistoryCommand(String line) {
            String[] tokens = line.split("[ |@]*");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
        }
        @Override
        public CommandType commandType() {
            return CommandType.HISTORY;
        }
    }
    class SleepCommand extends AbstractCommand {
        public final Duration duration;
        public SleepCommand(Duration duration) {
            this.duration = duration;
        }
        @Override
        public CommandType commandType() {
            return CommandType.SLEEP;
        }
    }
    class HelpCommand extends AbstractCommand {
        public final CommandType commandTypeToHelp;
        public HelpCommand(String line) throws Exception {
            String[] tokens = line.split("[ |@]*");
            this.commandTypeToHelp = CommandType.parse(tokens[commandType().getIndex(Field.Command)]);
        }
        @Override
        public CommandType commandType() {
            return CommandType.HISTORY;
        }
    }
    class QuitCommand extends AbstractCommand {
        @Override
        public CommandType commandType() {
            return CommandType.QUIT;
        }
    }
}
