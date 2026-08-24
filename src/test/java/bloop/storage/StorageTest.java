package bloop.storage;

import bloop.exception.DukeException;
import bloop.task.Deadline;
import bloop.task.Event;
import bloop.task.Task;
import bloop.task.TaskList;
import bloop.task.Todo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests file persistence for supported task types. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveThenLoad_mixedTasks_restoresDetailsAndStatuses() throws DukeException {
        Path dataFile = temporaryDirectory.resolve("data/duke.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read | book"));
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 1, 16));
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("team meeting", LocalDateTime.of(2026, 1, 16, 14, 0),
                LocalDateTime.of(2026, 1, 16, 16, 0)));

        Storage storage = new Storage(dataFile);
        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read | book", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Jan 16 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Jan 16 2026 14:00 to: Jan 16 2026 16:00)",
                loadedTasks.get(2).toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws DukeException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/duke.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_invalidRecord_throwsHelpfulException() throws Exception {
        Path dataFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(dataFile, "D | 2 | submit report | 2026-01-16\n");

        DukeException exception = assertThrows(DukeException.class, () -> new Storage(dataFile).load());
        assertEquals("OOPS!!! Your saved task data is invalid.", exception.getMessage());
    }
}
