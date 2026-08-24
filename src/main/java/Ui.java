import java.time.LocalDate;

/** Handles all console output formatting for Bloop. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____  _                   \n"
            + "| __ )| | ___   ___  _ __  \n"
            + "|  _ \\| |/ _ \\ / _ \\| '_ \\ \n"
            + "| |_) | | (_) | (_) | |_) |\n"
            + "|____/|_|\\___/ \\___/| .__/ \n"
            + "                    |_|    ";

    public void showWelcome() {
        showDivider();
        System.out.println(BANNER);
        System.out.println("Hello! I'm Bloop.");
        System.out.println("What can I do for you?");
        showDivider();
    }

    public void showDivider() {
        System.out.println(DIVIDER);
    }

    public void showError(DukeException exception) {
        System.out.println(" " + exception.getMessage());
    }

    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public void showStatusUpdated(Task task, boolean isDone) {
        System.out.println(isDone ? " Nice! I've marked this task as done:"
                : " OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    public void showFoundTasks(TaskList tasks, LocalDate date) {
        System.out.println(" Here are the tasks on " + date + ":");
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
    }
}
