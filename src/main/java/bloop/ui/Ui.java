package bloop.ui;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import bloop.exception.DukeException;
import bloop.task.Task;
import bloop.task.TaskList;

/** Handles all console output formatting for Bloop. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____  _                   \n"
            + "| __ )| | ___   ___  _ __  \n"
            + "|  _ \\| |/ _ \\ / _ \\| '_ \\ \n"
            + "| |_) | | (_) | (_) | |_) |\n"
            + "|____/|_|\\___/ \\___/| .__/ \n"
            + "                    |_|    ";
    private final Consumer<String> messageConsumer;

    /** Creates a console user interface. */
    public Ui() {
        this(null);
    }

    /** Creates a user interface that sends messages to the supplied consumer. */
    public Ui(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    /** Displays the chatbot greeting. */
    public void showWelcome() {
        if (messageConsumer == null) {
            showDivider();
            System.out.println(BANNER);
            System.out.println("Hello! I'm Bloop.");
            System.out.println("What can I do for you?");
            showDivider();
        } else {
            showMessage("Hello! I'm Bloop.", "What can I do for you?");
        }
    }

    /** Displays a divider between conversation turns. */
    public void showDivider() {
        if (messageConsumer == null) {
            System.out.println(DIVIDER);
        }
    }

    /** Displays a user-facing exception message. */
    public void showError(DukeException exception) {
        showMessage(exception.getMessage());
    }

    /** Displays the farewell message. */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage("Got it. I've added this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessage("Noted. I've removed this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation that a task's completion status changed. */
    public void showStatusUpdated(Task task, boolean isDone) {
        showMessage(isDone ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:", "  " + task);
    }

    /** Displays every task in the list. */
    public void showTaskList(TaskList tasks) {
        String taskLines = IntStream.range(0, tasks.size())
                .mapToObj(index -> formatTaskLine(index, tasks.get(index)))
                .collect(Collectors.joining());
        showMessage("Here are the tasks in your list:" + taskLines);
    }

    /** Displays tasks whose descriptions contain a supplied keyword. */
    public void showFoundTasks(TaskList tasks, String keyword) {
        String taskLines = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).containsKeyword(keyword))
                .mapToObj(index -> formatTaskLine(index, tasks.get(index)))
                .collect(Collectors.joining());
        showMessage("Here are the matching tasks in your list:" + taskLines);
    }

    /** Returns one numbered task line for a task's zero-based list index. */
    private String formatTaskLine(int index, Task task) {
        return "\n" + (index + 1) + "." + task;
    }

    /** Displays one or more lines as a single chatbot message. */
    private void showMessage(String... lines) {
        String message = String.join("\n", lines);
        if (messageConsumer == null) {
            System.out.println(" " + message);
        } else {
            messageConsumer.accept(message);
        }
    }
}
