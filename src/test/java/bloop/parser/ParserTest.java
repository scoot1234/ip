package bloop.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bloop.exception.DukeException;

/** Tests find-command keyword parsing. */
public class ParserTest {
    /** Verifies that a find keyword is trimmed without changing its content. */
    @Test
    public void parseFindKeyword_keywordWithWhitespace_returnsTrimmedKeyword() throws DukeException {
        assertEquals("read book", Parser.parseFindKeyword("find   read book  "));
    }

    /** Verifies that find commands require a keyword. */
    @Test
    public void parseFindKeyword_missingKeyword_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class, () -> Parser.parseFindKeyword("find"));

        assertEquals("OOPS!!! Please specify a keyword to find.", exception.getMessage());
    }
}
