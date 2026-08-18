/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline with a description and due time.
     *
     * @param description description of the task
     * @param by due date or time
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
