package bloop.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns a display representation prefixed with the to-do type icon. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
