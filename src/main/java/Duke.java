import java.util.Scanner;

/**
 * A chatbot that stores and displays tasks entered during the current session.
 */
public class Duke {
    public static void main(String[] args) {
        String divider = "____________________________________________________________";
        String banner = " ____  _                   \n"
                + "| __ )| | ___   ___  _ __  \n"
                + "|  _ \\| |/ _ \\ / _ \\| '_ \\ \n"
                + "| |_) | | (_) | (_) | |_) |\n"
                + "|____/|_|\\___/ \\___/| .__/ \n"
                + "                    |_|    ";

        System.out.println(divider);
        System.out.println(banner);
        System.out.println("Hello! I'm Bloop.");
        System.out.println("What can I do for you?");
        System.out.println(divider);

        Task[] tasks = new Task[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            }

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].unmarkAsDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
            }
            System.out.println(divider);
        }
    }
}
