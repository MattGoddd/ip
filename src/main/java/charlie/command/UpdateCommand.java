package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

public class UpdateCommand extends Command {
    /** Zero-based index of the task to mark. */
    private final int index;
    /** Field to change. */
    private final UpdateField updateField;
    /** Value to change the field into */
    private final String newValue;

    public UpdateCommand(int index, UpdateField updateField, String newValue) {
        this.index = index;
        this.updateField = updateField;
        this.newValue = newValue;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = tasks.get(this.index);
        tasks.replace(this.index, task.createUpdatedTask(this.updateField, this.newValue));

    }
}
