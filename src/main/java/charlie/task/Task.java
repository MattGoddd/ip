package charlie.task;

import charlie.command.UpdateField;
import charlie.exception.CharlieException;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** Description supplied by the user. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task with its description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is completed.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markUndone() {
        this.isDone = false;
    }

    /**
     * Creates an independent copy of this task with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied task with the requested status.
     */
    public abstract Task copyWithStatus(boolean isDone);

    /**
     * Returns the shared display representation of a task.
     *
     * @return Completion status and description.
     */
    @Override
    public String toString() {
        String status = (this.isDone) ? "X" : " ";
        return "[" + status + "] " + this.description;
    }

    /**
     * Converts this task to one line in Charlie's save-file format.
     *
     * @return The task data to write to the save file.
     */
    public abstract String saveFileFormat();

    /**
     * Creates a copy of this task with one field replaced.
     *
     * @param updateField Field to replace.
     * @param newValue Replacement value for the field.
     * @return Updated copy of this task.
     * @throws CharlieException If the field is unsupported or the replacement value is invalid.
     */
    public abstract Task createUpdatedTask(UpdateField updateField, String newValue);
}
