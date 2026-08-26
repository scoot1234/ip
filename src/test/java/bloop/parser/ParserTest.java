package bloop.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bloop.exception.DukeException;
import org.junit.jupiter.api.Test;

/** Tests find-command keyword parsing. */
public class ParserTest {
    @Test
    public void parseFindKeyword_keywordWithWhitespace_returnsTrimmedKeyword() throws DukeException {
        assertEquals("read book", Parser.parseFindKeyword("find   read book  "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsHelpfulException() {
        DukeException exception = assertThrows(DukeException.class,
                () -> Parser.parseFindKeyword("find"));

        assertEquals("OOPS!!! Please specify a keyword to find.", exception.getMessage());
    }
}
