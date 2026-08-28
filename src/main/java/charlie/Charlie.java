package charlie;

import java.time.LocalDate;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private final Storage storage;
    private final Ui ui;
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
                    case BYE:
                        ui.showOutro();
                        break commandLoop;
                    case LIST:
                        new ListCommand().execute(tasks, ui, storage);
                        break;
                    case ON:
                        showTasksOnDate(Parser.parseDate(input));
                        break;
                    case MARK: {
                        int index = Parser.parseTaskIndex(input, tasks.getSize());
                        mark(index);
                        break;
                    }
                    case UNMARK: {
                        int index = Parser.parseTaskIndex(input, tasks.getSize());
                        unmark(index);
                        break;
                    }
                    case DELETE: {
                        int index = Parser.parseTaskIndex(input, tasks.getSize());
                        deleteTask(index);
                        break;
                    }
                    case TODO:
                        // Fallthrough
                    case DEADLINE:
                        // Fallthrough
                    case EVENT: {
                        Task newTask = Parser.parseTask(input, commandType);
                        addTask(newTask);
                        break;
                    }
                }
            } catch (CharlieException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showHorizontalLine();
        }
    }

    private void mark(int index) {
        Task currentTask = tasks.mark(index);
        storage.save(tasks.getTasks());
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + currentTask.toString());
    }

    private void unmark(int index) {
        Task currentTask = tasks.unmark(index);
        storage.save(tasks.getTasks());
        ui.showMessage("OK, I've marked this task not done yet:");
        ui.showMessage("  " + currentTask.toString());
    }

    /**
     * Prints deadlines and events that occur on the specified date.
     * Deadlines must match the date exactly, while events may span the date.
     *
     * @param searchDate Date to check.
     */
    private void showTasksOnDate(LocalDate searchDate) {
        ui.showMessage("Here are the tasks occurring on " + searchDate + ":");
        int matchCount = 0;
        for (Task task : tasks.findOnDate(searchDate)) {
            matchCount++;
            ui.showMessage(matchCount + "." + task);
        }

        if (matchCount == 0) {
            ui.showMessage("No deadlines or events occur on this date.");
        }
    }

    private void addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task.toString());
        ui.showMessage("Now you have " + tasks.getSize() + " tasks in the list.");
    }

    private void deleteTask(int index) {
        Task deletedTask = tasks.delete(index);
        storage.save(tasks.getTasks());
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + deletedTask.toString());
        ui.showMessage("Now you have " + tasks.getSize() + " tasks in the list.");
    }
}
