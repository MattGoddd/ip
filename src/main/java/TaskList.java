import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns Charlie's collection of tasks and provides operations on that collection.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param initialTasks tasks with which to initialise the list
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based task index
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index
     * @return selected task
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot suitable for saving or displaying.
     *
     * @return current tasks in list order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Finds deadlines and events that occur on the requested date.
     *
     * @param searchDate date on which tasks must occur
     * @return matching tasks in their original list order
     */
    public List<Task> findOnDate(LocalDate searchDate) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadlineTask
                    && deadlineTask.deadline.equals(searchDate)) {
                matches.add(task);
            } else if (task instanceof Event eventTask) {
                LocalDate fromDate = eventTask.from.toLocalDate();
                LocalDate toDate = eventTask.to.toLocalDate();
                if (!searchDate.isBefore(fromDate) && !searchDate.isAfter(toDate)) {
                    matches.add(task);
                }
            }
        }
        return matches;
    }
}
