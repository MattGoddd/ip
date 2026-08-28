package charlie;

/**
 * Marks one task as not done.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command for the task at the given zero-based index.
     *
     * @param index Zero-based index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Unmarks the selected task, saves the task list, and displays the result.
     *
     * @param tasks Task collection containing the selected task.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks.getTasksAfterChangingStatus(index, false));
        Task unmarkedTask = tasks.unmark(index);
        ui.showMessage("OK, I've marked this task not done yet:");
        ui.showMessage("  " + unmarkedTask);
    }
}
