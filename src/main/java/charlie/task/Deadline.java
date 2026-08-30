package charlie.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific calendar date.
 */
public class Deadline extends Task {
    /** Date by which this task must be completed. */
    protected LocalDate deadline;

    /**
     * Creates a deadline task with its description, status, and due date.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is completed.
     * @param deadline Date by which the task must be completed.
     */
    public Deadline(String description, boolean isDone, LocalDate deadline) {
        super(description, isDone);
        this.deadline = deadline;
    }

    /**
     * Creates a copy of this deadline with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied deadline with the requested status.
     */
    @Override
    public Task copyWithStatus(boolean isDone) {
        return new Deadline(description, isDone, deadline);
    }

    /**
     * Returns the display representation of this deadline.
     *
     * @return Deadline type, completion status, description, and due date.
     */
    @Override
    public String toString() {
        String formattedDeadline = this.deadline.format(
                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return "[D]" + super.toString() + " (by: " + formattedDeadline + ")";
    }

    /**
     * Converts this deadline into the format used in the save file.
     *
     * @return Serialized deadline data.
     */
    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "D" + " | " + status + " | " + this.description + " | " + this.deadline;
    }
}
