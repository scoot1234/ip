package bloop.task;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Tests task-list ordering and defensive copying. */
public class TaskListTest {
    @Test
    public void addGetRemoveAndRemoveLast_preservesTaskOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task inserted = new Todo("inserted");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);
        tasks.add(1, inserted);

        assertEquals(3, tasks.size());
        assertSame(inserted, tasks.get(1));
        assertSame(inserted, tasks.remove(1));
        assertSame(second, tasks.removeLast());
        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(0));
    }

    @Test
    public void asList_changedCopy_doesNotChangeTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> copy = tasks.asList();
        copy.clear();

        assertEquals(1, tasks.size());
    }
}
