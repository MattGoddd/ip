package charlie;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private Storage storage;
    private Ui ui;
    private TaskList tasks;

    /**
     * Creates Charlie with collaborators for user interaction and task persistence.
     *
     * @param filePath Path to the saved-task file.
     */
    public Charlie(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
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
                tasks = new TaskList(storage.load());
            } catch (CharlieException e) {
                ui.showLoadingError(e.getMessage());
                tasks = new TaskList();
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
        commandLoop:
        while (ui.hasNextCommand()) {
            try {
                String input = ui.readCommand();
                ui.showHorizontalLine();
                CommandType commandType = Parser.parseCommand(input);
                switch (commandType) {
                    case BYE: {
                        Command exitCommand = new ExitCommand();
                        exitCommand.execute(tasks, ui, storage);
                        if (exitCommand.isExit()) {
                            break commandLoop;
                        }
                        break;
                    }
                    case LIST:
                        new ListCommand().execute(tasks, ui, storage);
                        break;
                    case ON:
                        new FindCommand(Parser.parseDate(input)).execute(tasks, ui, storage);
                        break;
                    case MARK: {
                        int index = Parser.parseTaskIndex(input);
                        new MarkCommand(index).execute(tasks, ui, storage);
                        break;
                    }
                    case UNMARK: {
                        int index = Parser.parseTaskIndex(input);
                        new UnmarkCommand(index).execute(tasks, ui, storage);
                        break;
                    }
                    case DELETE: {
                        int index = Parser.parseTaskIndex(input);
                        new DeleteCommand(index).execute(tasks, ui, storage);
                        break;
                    }
                    case TODO:
                        // Fallthrough
                    case DEADLINE:
                        // Fallthrough
                    case EVENT: {
                        Task newTask = Parser.parseTask(input, commandType);
                        new AddCommand(newTask).execute(tasks, ui, storage);
                        break;
                    }
                }
            } catch (CharlieException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showHorizontalLine();
        }
    }

}
