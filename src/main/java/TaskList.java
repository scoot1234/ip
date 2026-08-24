import java.util.ArrayList;
import java.util.List;

/** Manages the collection of tasks in the current session. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Creates a task list from previously loaded tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task removeLast() {
        return tasks.remove(tasks.size() - 1);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
