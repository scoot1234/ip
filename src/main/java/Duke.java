import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

/**
 * A chatbot that stores, displays, and updates tasks entered during the current session.
 */
public class Duke {
    private static final String DIVIDER = "____________________________________________________________";
    private static final Path DATA_FILE = Path.of("data", "duke.txt");

    public static void main(String[] args) {
        String banner = " ____  _                   \n"
                + "| __ )| | ___   ___  _ __  \n"
                + "|  _ \\| |/ _ \\ / _ \\| '_ \\ \n"
                + "| |_) | | (_) | (_) | |_) |\n"
                + "|____/|_|\\___/ \\___/| .__/ \n"
                + "                    |_|    ";

        System.out.println(DIVIDER);
        System.out.println(banner);
        System.out.println("Hello! I'm Bloop.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        List<Task> tasks;
        try {
            tasks = loadTasks();
        } catch (DukeException exception) {
            System.out.println(" " + exception.getMessage());
            System.out.println(DIVIDER);
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            try {
                if (handleCommand(command, tasks)) {
                    System.out.println(DIVIDER);
                    break;
                }
            } catch (DukeException exception) {
                System.out.println(" " + exception.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }

    /**
     * Executes one supported command.
     *
     * @param command command entered by the user
     * @param tasks task list for the current session
     * @return whether the chatbot should exit
     * @throws DukeException if the command or its arguments are invalid
     */
    private static boolean handleCommand(String command, List<Task> tasks) throws DukeException {
        CommandType commandType = CommandType.fromInput(command);
        switch (commandType) {
        case BYE:
            System.out.println(" Bye. Hope to see you again soon!");
            return true;
        case LIST:
            printTaskList(tasks);
            return false;
        case MARK:
            updateTaskStatus(command, CommandType.MARK, tasks, true);
            return false;
        case UNMARK:
            updateTaskStatus(command, CommandType.UNMARK, tasks, false);
            return false;
        case DELETE:
            deleteTask(command, tasks);
            return false;
        case TODO:
            addTodo(command, tasks);
            return false;
        case DEADLINE:
            addDeadline(command, tasks);
            return false;
        case EVENT:
            addEvent(command, tasks);
            return false;
        case UNKNOWN:
            throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        default:
            throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Updates a task's completion status and displays its confirmation. */
    private static void updateTaskStatus(String command, CommandType commandType, List<Task> tasks,
                                         boolean isDone) throws DukeException {
        int taskIndex = getTaskIndex(command, commandType, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
        try {
            saveTasks(tasks);
        } catch (DukeException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.unmarkAsDone();
            }
            throw exception;
        }
        if (isDone) {
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("   " + task);
    }

    /** Removes the selected task and displays its confirmation. */
    private static void deleteTask(String command, List<Task> tasks) throws DukeException {
        int taskIndex = getTaskIndex(command, CommandType.DELETE, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        try {
            saveTasks(tasks);
        } catch (DukeException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Parses a to-do command, validates its description, and adds the task. */
    private static void addTodo(String command, List<Task> tasks) throws DukeException {
        String description = command.substring(CommandType.TODO.getKeyword().length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a todo cannot be empty.");
        }
        addTask(new Todo(description), tasks);
    }

    /**
     * Parses a deadline command, validates its details, and adds the deadline.
     */
    private static void addDeadline(String command, List<Task> tasks) throws DukeException {
        String details = command.substring(CommandType.DEADLINE.getKeyword().length()).trim();
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
        addTask(new Deadline(description, by), tasks);
    }

    /**
     * Parses an event command, validates its details, and adds the event.
     */
    private static void addEvent(String command, List<Task> tasks) throws DukeException {
        String details = command.substring(CommandType.EVENT.getKeyword().length()).trim();
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
        addTask(new Event(description, from, to), tasks);
    }

    /**
     * Adds a task to the in-memory list and displays its confirmation.
     */
    private static void addTask(Task task, List<Task> tasks) throws DukeException {
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (DukeException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Loads saved tasks from the application's data file.
     *
     * @return tasks restored from the data file, or an empty list when it does not exist
     * @throws DukeException if the data file cannot be read
     */
    private static List<Task> loadTasks() throws DukeException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }
        try {
            for (String taskLine : Files.readAllLines(DATA_FILE)) {
                if (!taskLine.isBlank()) {
                    tasks.add(deserializeTask(taskLine));
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new DukeException("OOPS!!! I could not load your tasks.");
        }
    }

    /**
     * Recreates one task from its line-based storage representation.
     *
     * @param taskLine one line from the data file
     * @return restored task
     */
    private static Task deserializeTask(String taskLine) throws DukeException {
        if (taskLine.startsWith("V2 | ")) {
            return deserializeVersionTwoTask(taskLine);
        }
        String[] taskParts = taskLine.split(" \\| ", -1);
        validateTaskParts(taskParts, false);
        return createTask(taskParts[0], taskParts[1], taskParts[2], taskParts);
    }

    /** Restores a version-two task record with Base64-encoded text fields. */
    private static Task deserializeVersionTwoTask(String taskLine) throws DukeException {
        String[] taskParts = taskLine.split(" \\| ", -1);
        validateTaskParts(taskParts, true);
        String description = decodeText(taskParts[3]);
        String[] decodedParts = taskParts.clone();
        decodedParts[3] = description;
        for (int i = 4; i < decodedParts.length; i++) {
            decodedParts[i] = decodeText(decodedParts[i]);
        }
        return createTask(decodedParts[1], decodedParts[2], description, decodedParts);
    }

    /** Validates a stored task's type, done status, and number of fields. */
    private static void validateTaskParts(String[] taskParts, boolean isVersionTwo) throws DukeException {
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

    /** Creates a task after its stored fields have been validated. */
    private static Task createTask(String type, String doneStatus, String description, String[] taskParts) {
        Task task;
        int detailStartIndex = taskParts[0].equals("V2") ? 4 : 3;
        switch (type) {
        case "D":
            task = new Deadline(description, taskParts[detailStartIndex]);
            break;
        case "E":
            task = new Event(description, taskParts[detailStartIndex], taskParts[detailStartIndex + 1]);
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

    /** Decodes a Base64 text field in a version-two data record. */
    private static String decodeText(String text) throws DukeException {
        try {
            return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new DukeException("OOPS!!! Your saved task data is invalid.");
        }
    }

    /**
     * Writes every task to the application's data file.
     *
     * @param tasks task list to save
     * @throws DukeException if the data file cannot be written
     */
    private static void saveTasks(List<Task> tasks) throws DukeException {
        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(serializeTask(task));
        }
        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, taskLines);
        } catch (IOException | SecurityException exception) {
            throw new DukeException("OOPS!!! I could not save your tasks.");
        }
    }

    /**
     * Converts a task into its line-based storage representation.
     *
     * @param task task to serialize
     * @return one line for the data file
     */
    private static String serializeTask(Task task) {
        String doneStatus = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "V2 | T | " + doneStatus + " | " + encodeText(task.getDescription());
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "V2 | D | " + doneStatus + " | " + encodeText(task.getDescription())
                    + " | " + encodeText(deadline.getBy());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "V2 | E | " + doneStatus + " | " + encodeText(task.getDescription())
                    + " | " + encodeText(event.getFrom()) + " | " + encodeText(event.getTo());
        }
        return "V2 | T | " + doneStatus + " | " + encodeText(task.getDescription());
    }

    /** Encodes a task text field so it can safely contain storage separators. */
    private static String encodeText(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parses and validates a one-based task number from a command.
     */
    private static int getTaskIndex(String command, CommandType commandType, int taskCount) throws DukeException {
        String taskNumberText = command.substring(commandType.getKeyword().length()).trim();
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

    /**
     * Displays every task in the in-memory list.
     */
    private static void printTaskList(List<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }
}
