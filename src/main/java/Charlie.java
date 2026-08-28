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
            readCommand();
        } finally {
            ui.close();
        }
    }

    /**
     * Reads commands until the user enters {@code bye}.
     * Other input is stored as a task, while {@code list} displays all stored tasks.
     */
    private void readCommand() {
        label:
        while (ui.hasNextCommand()) {
            try {
                String input = ui.readCommand();
                ui.showHorizontalLine();
                Command command = Parser.parseCommand(input);
                switch (command) {
                    case BYE:
                        ui.showOutro();
                        break label;
                    case LIST:
                        showTasks();
                        break;
                    case ON:
                        showTasksOnDate(Parser.parseDate(input));
                        break;
                    case MARK: {
                        int index = Parser.parseTaskIndex(input, tasks.size());
                        mark(index);
                        break;
                    }
                    case UNMARK: {
                        int index = Parser.parseTaskIndex(input, tasks.size());
                        unmark(index);
                        break;
                    }
                    case DELETE: {
                        int index = Parser.parseTaskIndex(input, tasks.size());
                        deleteTask(index);
                        break;
                    }
                    case TODO:
                    case DEADLINE:
                    case EVENT: {
                        Task newTask = Parser.parseTask(input, command);
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
        Task curTask = tasks.mark(index);
        storage.save(tasks.getTasks());
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + curTask.toString());
    }

    private void unmark(int index) {
        Task curTask = tasks.unmark(index);
        storage.save(tasks.getTasks());
        ui.showMessage("OK, I've marked this task not done yet:");
        ui.showMessage("  " + curTask.toString());
    }

    private void showTasks() {
        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
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
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private void deleteTask(int index) {
        Task deletedTask = tasks.delete(index);
        storage.save(tasks.getTasks());
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + deletedTask.toString());
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

}
