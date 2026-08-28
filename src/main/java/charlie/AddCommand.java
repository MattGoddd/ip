package charlie;

/**
 * Adds one task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command for the task to add.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the task list, and displays the result.
     *
     * @param tasks Task collection to update.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task);
        ui.showMessage("Now you have " + tasks.getSize() + " tasks in the list.");
    }
}
