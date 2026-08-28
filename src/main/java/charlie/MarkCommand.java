package charlie;

/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command for the task at the given zero-based index.
     *
     * @param index Zero-based index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the selected task, saves the task list, and displays the result.
     *
     * @param tasks Task collection containing the selected task.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task markedTask = tasks.mark(index);
        storage.save(tasks.getTasks());
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + markedTask);
    }
}
