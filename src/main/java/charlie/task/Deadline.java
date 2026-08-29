package charlie.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific calendar date.
 */
public class Deadline extends Task {
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

    @Override
    public Task copyWithStatus(boolean isDone) {
        return new Deadline(description, isDone, deadline);
    }

    @Override
    public String toString() {
        String formattedDeadline = this.deadline.format(
                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return "[D]" + super.toString() + " (by: " + formattedDeadline + ")";
    }

    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "D" + " | " + status + " | " + this.description + " | " + this.deadline;
    }
}
