package charlie.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import charlie.exception.CharlieException;

/**
 * Owns Charlie's collection of tasks and provides operations on that collection.
 */
public class TaskList {
    /** Tasks in their current list order. */
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
     * @param initialTasks Tasks with which to initialize the list.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task state that would result from adding a task.
     *
     * @param task Task to include in the proposed state.
     * @return Proposed tasks without changing this task list.
     */
    public List<Task> getTasksAfterAdding(Task task) {
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.add(task);
        return List.copyOf(proposedTasks);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index Zero-based task index.
     * @return Selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Marks and returns the task at the given zero-based index as done.
     *
     * @param index Zero-based task index.
     * @return Marked task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task mark(int index) {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markDone();
        return task;
    }

    /**
     * Marks and returns the task at the given zero-based index as not done.
     *
     * @param index Zero-based task index.
     * @return Unmarked task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task unmark(int index) {
        validateIndex(index);
        Task task = tasks.get(index);
        task.markUndone();
        return task;
    }

    /**
     * Deletes and returns the task at the given zero-based index.
     *
     * @param index Zero-based task index.
     * @return Deleted task.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public Task delete(int index) {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task state that would result from deleting a task.
     *
     * @param index Zero-based index of the task to omit.
     * @return Proposed tasks without changing this task list.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public List<Task> getTasksAfterDeleting(int index) {
        validateIndex(index);
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.remove(index);
        return List.copyOf(proposedTasks);
    }

    /**
     * Returns the task state that would result from changing one task's status.
     *
     * @param index Zero-based index of the task to copy with a new status.
     * @param isDone Completion status for the copied task.
     * @return Proposed tasks without changing this task list.
     * @throws CharlieException If the index does not identify an existing task.
     */
    public List<Task> getTasksAfterChangingStatus(int index, boolean isDone) {
        validateIndex(index);
        List<Task> proposedTasks = new ArrayList<>(tasks);
        proposedTasks.set(index, tasks.get(index).copyWithStatus(isDone));
        return List.copyOf(proposedTasks);
    }

    /**
     * Verifies that a zero-based task index exists in the current list.
     *
     * @param index Zero-based task index to validate.
     * @throws CharlieException If the task list is empty or the index is outside it.
     */
    private void validateIndex(int index) {
        if (tasks.isEmpty()) {
            throw new CharlieException("There are no tasks in the list.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new CharlieException(
                    "Please enter a task number from 1 to " + tasks.size() + ".");
        }
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return Task count.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot suitable for saving or displaying.
     *
     * @return Current tasks in list order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Finds deadlines and events that occur on the requested date.
     *
     * @param searchDate Date on which tasks must occur.
     * @return Matching tasks in their original list order.
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

    /**
     * Finds tasks whose descriptions contain the requested keyword or phrase.
     *
     * @param keyword Keyword or phrase to search for using case-insensitive matching.
     * @return Matching tasks in their original list order.
     */
    public List<Task> findByKeyword(String keyword) {
        List<Task> matches = new ArrayList<>();
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        for (Task task : tasks) {
            String normalizedDescription = task.description.toLowerCase(Locale.ROOT);
            if (normalizedDescription.contains(normalizedKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
