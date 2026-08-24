package bloop.parser;

import bloop.exception.DukeException;
import bloop.task.Deadline;
import bloop.task.Event;
import bloop.task.Todo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests command parsing and validation. */
public class ParserTest {
    @Test
    public void parseCommandType_supportedAndUnknownInputs_returnsExpectedType() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("listing"));
    }

    @Test
    public void parseTodo_validAndBlankDescriptions_createsTaskOrThrowsHelpfulException() throws DukeException {
        Todo todo = Parser.parseTodo("todo read book");
        assertEquals("read book", todo.getDescription());

        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseTodo("todo"));
        assertEquals("OOPS!!! The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseDeadline_validAndInvalidDetails_createsTaskOrThrowsHelpfulException() throws DukeException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2026-01-16");
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 1, 16), deadline.getBy());

        DukeException exception = assertThrows(DukeException.class,
                () -> Parser.parseDeadline("deadline submit report /by Friday"));
        assertEquals("OOPS!!! Use a deadline date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseEvent_validCommand_createsEventWithParsedDetails() throws DukeException {
        Event event = Parser.parseEvent("event team meeting /from 2026-01-16 14:00 /to 2026-01-16 16:30");

        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 16, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 1, 16, 16, 30), event.getTo());
    }

    @Test
    public void parseEvent_missingMarkers_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class,
                () -> Parser.parseEvent("event team meeting /from 2026-01-16 14:00"));

        assertEquals("OOPS!!! Use event <description> /from <start> /to <end>.", exception.getMessage());
    }

    @Test
    public void parseEvent_missingDescription_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class,
                () -> Parser.parseEvent("event /from 2026-01-16 14:00 /to 2026-01-16 16:00"));

        assertEquals("OOPS!!! The description of an event cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseEvent_invalidDateTime_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class,
                () -> Parser.parseEvent("event team meeting /from Friday /to 2026-01-16 16:00"));

        assertEquals("OOPS!!! Use event date/times in yyyy-MM-dd HH:mm format.", exception.getMessage());
    }

    @Test
    public void parseFindDate_validAndInvalidDates_returnsDateOrThrowsHelpfulException() throws DukeException {
        assertEquals(LocalDate.of(2026, 1, 16), Parser.parseFindDate("find 2026-01-16"));

        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseFindDate("find"));
        assertEquals("OOPS!!! Please specify a date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validAndInvalidNumbers_returnsZeroBasedIndexOrThrowsHelpfulException()
            throws DukeException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", CommandType.MARK, 3));

        DukeException nonNumber = assertThrows(DukeException.class,
                () -> Parser.parseTaskIndex("mark two", CommandType.MARK, 3));
        assertEquals("OOPS!!! The task number must be a whole number.", nonNumber.getMessage());

        DukeException outOfRange = assertThrows(DukeException.class,
                () -> Parser.parseTaskIndex("mark 4", CommandType.MARK, 3));
        assertEquals("OOPS!!! The task number must refer to an existing task.", outOfRange.getMessage());
    }
}
