package charlie;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with its description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is completed.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "T" + " | " + status + " | " + this.description;
    }
}
