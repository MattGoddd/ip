package charlie.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import charlie.command.CommandType;
import charlie.exception.CharlieException;

public class ParserTest {
    @Test
    public void parseCommand_emptyInput_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class,
                () -> Parser.parseCommand("")
        );
        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parseDate_invalidDateFormat_exceptionThrown() {
        String invalidInput = "on 09-15-2026";
        CharlieException exception = assertThrows(
                CharlieException.class,
                () -> Parser.parseDate(invalidInput)
        );
        assertEquals("Date must be a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_multipleWords_returnsEntireSearchText() {
        assertEquals("return book", Parser.parseFindKeyword("find return book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class,
                () -> Parser.parseFindKeyword("find")
        );
        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }

    @Test
    public void parseTask_eventEndsBeforeStart_exceptionThrown() {
        String invalidInput = "event meeting /from 2026-09-01 1600 /to 2026-09-01 1400";
        CharlieException exception = assertThrows(
                CharlieException.class,
                () -> Parser.parseTask(invalidInput, CommandType.EVENT)
        );
        assertEquals("Event end must be after its start.", exception.getMessage());
    }
}
