import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific calendar date.
 */
public class Deadline extends Task {
    protected LocalDate deadline;

    public Deadline(String description, boolean isDone, LocalDate deadline) {
        super(description, isDone);
        this.deadline = deadline;
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
