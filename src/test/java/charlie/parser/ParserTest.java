package charlie.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import charlie.command.CommandType;
import charlie.exception.CharlieException;
import charlie.task.Task;

public class ParserTest {
    @Test
    public void parseCommand_emptyInput_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseCommand(""));
        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parseCommand_nullInput_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseCommand(null));
        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parse_listWithUnexpectedArgument_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("list extra"));
        assertEquals("The list command does not accept arguments.", exception.getMessage());
    }

    @Test
    public void parseDate_invalidDateFormat_exceptionThrown() {
        String invalidInput = "on 09-15-2026";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseDate(invalidInput));
        assertEquals("Date must be a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_multipleWords_returnsEntireSearchText() {
        assertEquals("return book", Parser.parseFindKeyword("find return book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseFindKeyword("find"));
        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }

    @Test
    public void parseTask_eventEndsBeforeStart_exceptionThrown() {
        String invalidInput = "event meeting /from 2026-09-01 1600 /to 2026-09-01 1400";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.EVENT));
        assertEquals("Event end must be after its start.", exception.getMessage());
    }

    @Test
    public void parseTask_repeatedDescriptionWhitespace_returnsNormalizedDescription() {
        Task task = Parser.parseTask("todo   borrow    book  ", CommandType.TODO);
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    public void parseTask_duplicateDeadlineDelimiter_exceptionThrown() {
        String invalidInput = "deadline return book /by 2026-09-20 /by 2026-09-21";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.DEADLINE));
        assertEquals("A deadline must include /by exactly once.", exception.getMessage());
    }

    @Test
    public void parseTask_duplicateEventDelimiter_exceptionThrown() {
        String invalidInput = "event meeting /from 2026-09-20 0900 /from 2026-09-20 1000 "
                + "/to 2026-09-20 1100";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.EVENT));
        assertEquals(
                "An event must include /from and /to exactly once.",
                exception.getMessage());
    }

    @Test
    public void parseTask_descriptionContainingSaveDelimiter_exceptionThrown() {
        String invalidInput = "todo review A | B";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.TODO));
        assertEquals("A task description cannot contain |.", exception.getMessage());
    }

    @Test
    public void parseUpdate_missingArguments_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("update 1 description"));
        assertEquals("Usage: update TASK_NUMBER FIELD NEW_VALUE.", exception.getMessage());
    }

    @Test
    public void parseUpdate_nonNumericTaskNumber_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("update first description changed"));
        assertEquals("Please enter a valid task number.", exception.getMessage());
    }

    @Test
    public void parseUpdate_unsupportedField_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("update 1 priority high"));
        assertEquals(
                "Supported update fields are description, deadline, from, and to.",
                exception.getMessage());
    }
}
