package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

public class UpdateCommand extends Command {
    /** Zero-based index of the task to mark. */
    private final int index;
    /** Field to change. */
    private final String field;
    /** Value to change the field into */
    private final String newValue;

    public UpdateCommand(int index, String field, String newValue) {
        this.index = index;
        this.field = field;
        this.newValue = newValue;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {

    }
}
