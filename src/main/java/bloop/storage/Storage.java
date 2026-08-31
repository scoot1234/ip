package bloop.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import bloop.exception.DukeException;
import bloop.task.Deadline;
import bloop.task.Event;
import bloop.task.Task;
import bloop.task.TaskList;
import bloop.task.Todo;

/** Loads and saves task data in the application's line-based storage file. */
public class Storage {
    private final Path dataFile;

    /** Creates task storage backed by the supplied data file. */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /** Returns all tasks loaded from storage, or an empty list when no file exists. */
    public List<Task> load() throws DukeException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }
        try {
            for (String taskLine : Files.readAllLines(dataFile)) {
                if (!taskLine.isBlank()) {
                    tasks.add(deserializeTask(taskLine));
                }
            }
            return tasks;
        } catch (IOException | SecurityException | DateTimeParseException exception) {
            throw new DukeException("OOPS!!! I could not load your tasks.");
        }
    }

    /** Saves all tasks to storage. */
    public void save(TaskList tasks) throws DukeException {
        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks.asList()) {
            taskLines.add(serializeTask(task));
        }
        try {
            Files.createDirectories(dataFile.getParent());
            Files.write(dataFile, taskLines);
        } catch (IOException | SecurityException exception) {
            throw new DukeException("OOPS!!! I could not save your tasks.");
        }
    }

    /** Returns a task restored from a legacy or version-two data record. */
    private Task deserializeTask(String taskLine) throws DukeException {
        if (taskLine.startsWith("V2 | ")) {
            return deserializeVersionTwoTask(taskLine);
        }
        String[] taskParts = taskLine.split(" \\| ", -1);
        validateTaskParts(taskParts, false);
        return createTask(taskParts[0], taskParts[1], taskParts[2], taskParts);
    }

    /** Returns a task restored from a version-two encoded data record. */
    private Task deserializeVersionTwoTask(String taskLine) throws DukeException {
        String[] taskParts = taskLine.split(" \\| ", -1);
        validateTaskParts(taskParts, true);
        String[] decodedParts = taskParts.clone();
        for (int i = 3; i < decodedParts.length; i++) {
            decodedParts[i] = decodeText(decodedParts[i]);
        }
        return createTask(decodedParts[1], decodedParts[2], decodedParts[3], decodedParts);
    }

    /** Validates the type, status, and field count of stored task fields. */
    private void validateTaskParts(String[] taskParts, boolean isVersionTwo) throws DukeException {
        int typeIndex = isVersionTwo ? 1 : 0;
        int statusIndex = isVersionTwo ? 2 : 1;
        int expectedTodoParts = isVersionTwo ? 4 : 3;
        int expectedDeadlineParts = isVersionTwo ? 5 : 4;
        int expectedEventParts = isVersionTwo ? 6 : 5;
        if (taskParts.length < expectedTodoParts
                || (!taskParts[statusIndex].equals("0") && !taskParts[statusIndex].equals("1"))) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
        String type = taskParts[typeIndex];
        int expectedParts = type.equals("T") ? expectedTodoParts
                : type.equals("D") ? expectedDeadlineParts : type.equals("E") ? expectedEventParts : -1;
        if (expectedParts != taskParts.length) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
    }

    /** Returns the task subtype created from validated stored fields. */
    private Task createTask(String type, String doneStatus, String description, String[] taskParts) {
        int detailStartIndex = taskParts[0].equals("V2") ? 4 : 3;
        Task task;
        switch (type) {
            case "D":
                task = new Deadline(description, LocalDate.parse(taskParts[detailStartIndex]));
                break;
            case "E":
                task = new Event(description, LocalDateTime.parse(taskParts[detailStartIndex]),
                        LocalDateTime.parse(taskParts[detailStartIndex + 1]));
                break;
            case "T":
            default:
                task = new Todo(description);
                break;
        }
        if (doneStatus.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Returns a version-two data record for a task. */
    private String serializeTask(Task task) {
        String doneStatus = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "V2 | D | " + doneStatus + " | " + encodeText(task.getDescription())
                    + " | " + encodeText(deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "V2 | E | " + doneStatus + " | " + encodeText(task.getDescription())
                    + " | " + encodeText(event.getFrom().toString()) + " | " + encodeText(event.getTo().toString());
        }
        return "V2 | T | " + doneStatus + " | " + encodeText(task.getDescription());
    }

    /** Returns Base64-encoded text that safely preserves storage separators. */
    private String encodeText(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns decoded Base64 text or reports invalid saved data. */
    private String decodeText(String text) throws DukeException {
        try {
            return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
    }
}
