import java.util.Scanner;

/**
 * A chatbot that stores, displays, and updates tasks entered during the current session.
 */
public class Duke {
    private static final int MAX_TASKS = 100;
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

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            try {
                taskCount = handleCommand(command, tasks, taskCount);
            } catch (DukeException exception) {
                System.out.println(" " + exception.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }

    /**
     * Executes one supported command and returns the updated number of tasks.
     *
     * @param command command entered by the user
     * @param tasks task storage for the current session
     * @param taskCount current number of tasks
     * @return updated number of tasks
     * @throws DukeException if the command or its arguments are invalid
     */
    private static int handleCommand(String command, Task[] tasks, int taskCount) throws DukeException {
        if (command.equals("list")) {
            printTaskList(tasks, taskCount);
            return taskCount;
        }
        if (command.equals("mark") || command.startsWith("mark ")) {
            int taskIndex = getTaskIndex(command, "mark", taskCount);
            tasks[taskIndex].markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + tasks[taskIndex]);
            return taskCount;
        }
        if (command.equals("unmark") || command.startsWith("unmark ")) {
            int taskIndex = getTaskIndex(command, "unmark", taskCount);
            tasks[taskIndex].unmarkAsDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + tasks[taskIndex]);
            return taskCount;
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new DukeException("OOPS!!! The description of a todo cannot be empty.");
            }
            return addTask(new Todo(description), tasks, taskCount);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return addDeadline(command, tasks, taskCount);
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return addEvent(command, tasks, taskCount);
        }
        throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Parses a deadline command, validates its details, and adds the deadline.
     */
    private static int addDeadline(String command, Task[] tasks, int taskCount) throws DukeException {
        String details = command.substring("deadline".length()).trim();
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
        return addTask(new Deadline(description, by), tasks, taskCount);
    }

    /**
     * Parses an event command, validates its details, and adds the event.
     */
    private static int addEvent(String command, Task[] tasks, int taskCount) throws DukeException {
        String details = command.substring("event".length()).trim();
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
        return addTask(new Event(description, from, to), tasks, taskCount);
    }

    /**
     * Adds a task to the in-memory list and displays its confirmation.
     */
    private static int addTask(Task task, Task[] tasks, int taskCount) throws DukeException {
        if (taskCount == MAX_TASKS) {
            throw new DukeException("OOPS!!! The task list is full.");
        }
        tasks[taskCount] = task;
        int updatedTaskCount = taskCount + 1;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    /**
     * Parses and validates a one-based task number from a mark or unmark command.
     */
    private static int getTaskIndex(String command, String commandWord, int taskCount) throws DukeException {
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new DukeException("OOPS!!! Please specify a task number to " + commandWord + ".");
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
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }
}
