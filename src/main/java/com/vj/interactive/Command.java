package com.vj.interactive;

import com.vj.model.attribute.Side;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Command
 *
 * --Usage--
 * Command: Field [Field] [Field]
 * new: MSFT B 1000 123.4
 *
 */
public interface Command {

    /**
     * Possible Fields (with command at index 0)
     */
    enum Field {
        Command,
        Symbol,
        Side,
        Qty,
        Price,
        OrderId;
    }

    /**
     * Commands + Fields (expected)
     */
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

        /**
         * Get the index (position) expected for the field.
         */
        public int getIndex(Field field) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i] == field) {
                    return i;
                }
            }
            /**
             * Asked for an index for a Field that isn't expected with this Command
             */
            throw new IllegalArgumentException("Field: " + field.name() + " is not part of the command: " + name());
        }

        /**
         * Which command is this?
         */
        public static CommandType parse(String value) throws Exception {
            for (CommandType commandType : CommandType.values()) {
                if (commandType.name().equalsIgnoreCase(value)) {
                    return commandType;
                }
            }
            throw new Exception("Command unknown: " + value);
        }
    }

    default <T> T get() { return (T) this; }
    CommandType commandType();

    SleepCommand SleepCommand = new SleepCommand(Duration.ofMillis(100));
    QuitCommand QuitCommand = new QuitCommand();

    class NewOrderCommand implements Command {
        private static final AtomicLong nextId = new AtomicLong(0);
        public final long orderId = nextId.getAndIncrement();
        public final String symbol;
        public final Side side;
        public final double qty;
        public final double price;
        public NewOrderCommand(String line) {
            String[] tokens = line.split(" |@");
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
    class CancelOrderCommand implements Command {
        public final String orderId;
        public CancelOrderCommand(String line) {
            String[] tokens = line.split(" |@");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
        }
        @Override
        public CommandType commandType() {
            return CommandType.CANCEL;
        }
    }
    class ModifyOrderCommand implements Command {
        public final String orderId;
        public final double qty;
        public final double price;
        public ModifyOrderCommand(String line) {
            String[] tokens = line.split(" |@");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
            this.qty = Double.parseDouble(tokens[commandType().getIndex(Field.Qty)]);
            this.price = Double.parseDouble(tokens[commandType().getIndex(Field.Price)]);
        }
        @Override
        public CommandType commandType() {
            return CommandType.MODIFY;
        }
    }
    class OrderStatusCommand implements Command {
        public final String orderId;
        public OrderStatusCommand(String line) {
            String[] tokens = line.split(" |@");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
        }
        @Override
        public CommandType commandType() {
            return CommandType.STATUS;
        }
    }
    class OrderHistoryCommand implements Command {
        public final String orderId;
        public OrderHistoryCommand(String line) {
            String[] tokens = line.split(" |@");
            this.orderId = tokens[commandType().getIndex(Field.OrderId)];
        }
        @Override
        public CommandType commandType() {
            return CommandType.HISTORY;
        }
    }
    class SleepCommand implements Command {
        public final Duration duration;
        public SleepCommand(Duration duration) {
            this.duration = duration;
        }
        @Override
        public CommandType commandType() {
            return CommandType.SLEEP;
        }
    }
    class HelpCommand implements Command {
        public final CommandType commandTypeToHelp;
        public HelpCommand(String line) throws Exception {
            String[] tokens = line.split(" |@");
            this.commandTypeToHelp = CommandType.parse(tokens[0]);
        }
        @Override
        public CommandType commandType() {
            return CommandType.HELP;
        }
    }
    class QuitCommand implements Command {
        @Override
        public CommandType commandType() {
            return CommandType.QUIT;
        }
    }
}
