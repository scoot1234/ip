package bloop.parser;

import bloop.exception.DukeException;
import bloop.task.Deadline;
import bloop.task.Event;
import bloop.task.Todo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Parses user commands and validates their arguments. */
public class Parser {
    private static final DateTimeFormatter EVENT_INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Identifies the command type represented by user input. */
    public static CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /** Parses a to-do command into a task. */
    public static Todo parseTodo(String input) throws DukeException {
        String description = detailsAfter(input, CommandType.TODO);
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /** Parses a deadline command into a task with an ISO-8601 due date. */
    public static Deadline parseDeadline(String input) throws DukeException {
        String details = detailsAfter(input, CommandType.DEADLINE);
        if (details.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a deadline cannot be empty.");
        }
        int byIndex = details.indexOf(" /by ");
        if (byIndex == -1) {
            throw new DukeException("OOPS!!! Use deadline <description> /by <date/time>.");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new DukeException("OOPS!!! The deadline date/time cannot be empty.");
        }
        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException exception) {
            throw new DukeException("OOPS!!! Use a deadline date in yyyy-MM-dd format.");
        }
    }

    /** Parses an event command into a task with ISO-8601 start and end times. */
    public static Event parseEvent(String input) throws DukeException {
        String details = detailsAfter(input, CommandType.EVENT);
        if (details.isEmpty()) {
            throw new DukeException("OOPS!!! The description of an event cannot be empty.");
        }
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new DukeException("OOPS!!! Use event <description> /from <start> /to <end>.");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = details.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new DukeException("OOPS!!! Both event start and end date/time are required.");
        }
        try {
            return new Event(description, LocalDateTime.parse(from, EVENT_INPUT_FORMAT),
                    LocalDateTime.parse(to, EVENT_INPUT_FORMAT));
        } catch (DateTimeParseException exception) {
            throw new DukeException("OOPS!!! Use event date/times in yyyy-MM-dd HH:mm format.");
        }
    }

    /** Parses the ISO-8601 date supplied to a find command. */
    public static LocalDate parseFindDate(String input) throws DukeException {
        String dateText = detailsAfter(input, CommandType.FIND);
        if (dateText.isEmpty()) {
            throw new DukeException("OOPS!!! Please specify a date in yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new DukeException("OOPS!!! Use a find date in yyyy-MM-dd format.");
        }
    }

    /** Parses a one-based task number and returns its zero-based list index. */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount) throws DukeException {
        String taskNumberText = detailsAfter(input, commandType);
        if (taskNumberText.isEmpty()) {
            throw new DukeException("OOPS!!! Please specify a task number to " + commandType.getKeyword() + ".");
        }
        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new DukeException("OOPS!!! The task number must refer to an existing task.");
            }
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new DukeException("OOPS!!! The task number must be a whole number.");
        }
    }

    /** Extracts and trims the text following a command keyword. */
    private static String detailsAfter(String input, CommandType commandType) {
        return input.substring(commandType.getKeyword().length()).trim();
    }
}
