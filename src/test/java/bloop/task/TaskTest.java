package bloop.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests keyword matching against task descriptions. */
public class TaskTest {
    /** Verifies matching behavior for task-description keywords. */
    @Test
    public void containsKeyword_matchingAndMissingKeywords_returnsExpectedResult() {
        Task task = new Todo("read book");

        assertTrue(task.containsKeyword("book"));
        assertTrue(task.containsKeyword("read book"));
        assertFalse(task.containsKeyword("Book"));
        assertFalse(task.containsKeyword("return"));
    }
}
