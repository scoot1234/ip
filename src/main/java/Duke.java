import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A chatbot that stores, displays, and updates tasks entered during the current session.
 */
public class Duke {
    private static final String DIVIDER = "____________________________________________________________";

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

        List<Task> tasks = new ArrayList<>();
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
        if (isDone) {
            tasks.get(taskIndex).markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            tasks.get(taskIndex).unmarkAsDone();
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("   " + tasks.get(taskIndex));
    }

    /** Removes the selected task and displays its confirmation. */
    private static void deleteTask(String command, List<Task> tasks) throws DukeException {
        int taskIndex = getTaskIndex(command, CommandType.DELETE, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
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
    private static void addTask(Task task, List<Task> tasks) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
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
