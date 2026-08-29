package charlie.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import charlie.command.AddCommand;
import charlie.command.Command;
import charlie.command.CommandType;
import charlie.command.DeleteCommand;
import charlie.command.ExitCommand;
import charlie.command.FindCommand;
import charlie.command.ListCommand;
import charlie.command.MarkCommand;
import charlie.command.UnmarkCommand;
import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.Todo;

/**
 * Interprets raw user input and validates command arguments.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Converts user input into a command that is ready to execute.
     *
     * @param input Complete user input.
     * @return Command represented by the input.
     * @throws CharlieException If the command or its arguments are invalid.
     */
    public static Command parse(String input) {
        CommandType commandType = parseCommand(input);
        return switch (commandType) {
        case BYE -> new ExitCommand();
        case LIST -> new ListCommand();
        case ON -> new FindCommand(parseDate(input));
        case MARK -> new MarkCommand(parseTaskIndex(input));
        case UNMARK -> new UnmarkCommand(parseTaskIndex(input));
        case DELETE -> new DeleteCommand(parseTaskIndex(input));
        case TODO, DEADLINE, EVENT -> new AddCommand(parseTask(input, commandType));
        };
    }

    /**
     * Converts the first word of a user input line into a command.
     *
     * @param input Complete user input.
     * @return The recognized command.
     * @throws CharlieException If the input is empty or starts with an unknown command.
     */
    public static CommandType parseCommand(String input) {
        if (input.isBlank()) {
            throw new CharlieException("Please enter a command.");
        }
        String[] parts = input.trim().split("\\s+");
        return CommandType.parseKeyword(parts[0]);
    }

    /**
     * Parses the single date argument of an {@code on} command.
     *
     * @param input Complete user input.
     * @return The requested date.
     * @throws CharlieException If the argument count or date is invalid.
     */
    public static LocalDate parseDate(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            throw new CharlieException(
                    "Please provide exactly one date in yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(parts[1]);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Date must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses the one-based task number supplied to a task command.
     *
     * @param input Complete user input.
     * @return Zero-based task index.
     * @throws CharlieException If the input does not contain exactly one numeric task number.
     */
    public static int parseTaskIndex(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            throw new CharlieException("Please provide exactly one task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new CharlieException("Please enter a valid task number.");
        }

        return taskNumber - 1;
    }

    /**
     * Converts a task command into the corresponding task type.
     *
     * @param input Complete user input containing the task details.
     * @param commandType Type of task to create.
     * @return Task containing the parsed details.
     * @throws CharlieException If the command does not have the expected format.
     */
    public static Task parseTask(String input, CommandType commandType) {
        String[] commandAndArguments = input.trim().split("\\s+", 2);
        if (commandAndArguments.length < 2) {
            throw new CharlieException("The task description cannot be empty.");
        }

        String arguments = commandAndArguments[1];
        if (commandType == CommandType.TODO) {
            return new Todo(arguments, false);
        } else if (commandType == CommandType.DEADLINE) {
            return parseDeadline(arguments);
        } else {
            return parseEvent(arguments);
        }
    }

    /**
     * Parses the description and date fields of a deadline.
     */
    private static Task parseDeadline(String arguments) {
        int byPosition = arguments.indexOf("/by");
        if (byPosition == -1) {
            throw new CharlieException("A deadline must include /by followed by a date.");
        }
        String description = arguments.substring(0, byPosition).trim();
        if (description.isEmpty()) {
            throw new CharlieException("Description cannot be empty.");
        }
        String deadlineText = arguments.substring(byPosition + "/by".length()).trim();
        if (deadlineText.isBlank()) {
            throw new CharlieException("Deadline cannot be empty.");
        }
        try {
            return new Deadline(description, false, LocalDate.parse(deadlineText));
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Deadline must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses the description, start, and end fields of an event.
     */
    private static Task parseEvent(String arguments) {
        int fromPosition = arguments.indexOf("/from");
        int toPosition = arguments.indexOf("/to");
        if (fromPosition == -1 || toPosition == -1) {
            throw new CharlieException("Need to include /from or /to fields.");
        } else if (fromPosition > toPosition) {
            throw new CharlieException("Invalid argument format: /from should appear before /to");
        }

        String description = arguments.substring(0, fromPosition).trim();
        if (description.isEmpty()) {
            throw new CharlieException("Description cannot be empty");
        }
        String fromText = arguments.substring(
                fromPosition + "/from".length(), toPosition).trim();
        String toText = arguments.substring(toPosition + "/to".length()).trim();
        if (fromText.isBlank() || toText.isBlank()) {
            throw new CharlieException("from/to fields cannot be empty.");
        }

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDateTime from = LocalDateTime.parse(fromText, formatter);
            LocalDateTime to = LocalDateTime.parse(toText, formatter);
            if (!from.isBefore(to)) {
                throw new CharlieException("Event end must be after its start.");
            }
            return new Event(description, false, from, to);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Event dates must use the yyyy-MM-dd HHmm format.");
        }
    }
}
