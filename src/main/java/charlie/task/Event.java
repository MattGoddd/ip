package charlie.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event occurring between two specific date-times.
 */
public class Event extends Task {
    /** Date and time at which this event starts. */
    protected LocalDateTime startDateTime;

    /** Date and time at which this event ends. */
    protected LocalDateTime endDateTime;

    /**
     * Creates an event task with its description, status, start, and end.
     *
     * @param description Description of the event.
     * @param isDone Whether the event task is completed.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     */
    public Event(String description, boolean isDone, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description, isDone);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Creates a copy of this event with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied event with the requested status.
     */
    @Override
    public Task copyWithStatus(boolean isDone) {
        return new Event(description, isDone, startDateTime, endDateTime);
    }

    /**
     * Returns the display representation of this event.
     *
     * @return Event type, completion status, description, start, and end.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "MMM dd yyyy, h:mm a", Locale.ENGLISH);
        return "[E]" + super.toString() + " (from: " + this.startDateTime.format(formatter)
                + " to: " + this.endDateTime.format(formatter) + ")";
    }

    /**
     * Converts this event into the format used in the save file.
     *
     * @return Serialized event data.
     */
    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "E" + " | " + status + " | " + this.description + " | "
                + this.startDateTime + " | " + this.endDateTime;
    }
}
