package charlie;

import charlie.command.Command;
import charlie.exception.CharlieException;
import charlie.parser.Parser;
import charlie.storage.Storage;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    /** Saves and loads Charlie's task data. */
    private Storage storage;

    /** Handles console input and output. */
    private Ui ui;

    /** Stores the tasks in the current session. */
    private TaskList taskList;

    /**
     * Creates Charlie with collaborators for user interaction and task persistence.
     *
     * @param filePath Path to the saved-task file.
     */
    public Charlie(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.taskList = new TaskList();
    }

    /**
     * Starts Charlie using the default save-file location.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Charlie("data/charlie.txt").run();
    }

    /**
     * Loads saved tasks and processes commands until the user exits.
     */
    public void run() {
        try {
            ui.showIntro();
            try {
                taskList = new TaskList(storage.load());
            } catch (CharlieException e) {
                ui.showLoadingError(e.getMessage());
                taskList = new TaskList();
            }
            readCommands();
        } finally {
            ui.close();
        }
    }

    /**
     * Reads commands until the user enters {@code bye}.
     * Other input is stored as a task, while {@code list} displays all stored tasks.
     */
    private void readCommands() {
        while (ui.hasNextCommand()) {
            try {
                String input = ui.readCommand();
                ui.showHorizontalLine();
                Command command = Parser.parse(input);
                command.execute(taskList, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (CharlieException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showHorizontalLine();
        }
    }
}
