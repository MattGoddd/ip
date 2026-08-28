import java.time.LocalDate;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private static TaskList tasks = new TaskList();
    private static final Storage STORAGE = new Storage("data/charlie.txt");

    public static void main(String[] args) {
        try (Ui ui = new Ui()) {
            ui.showIntro();
            try {
                tasks = new TaskList(STORAGE.load());
            } catch (CharlieException e) {
                ui.showLoadingError(e.getMessage());
                tasks = new TaskList();
            }
            readCommand(ui);
        }
    }

    /**
     * Reads commands until the user enters {@code bye}.
     * Other input is stored as a task, while {@code list} displays all stored tasks.
     */
    private static void readCommand(Ui ui) {
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
                        listTask(ui);
                        break;
                    case ON:
                        checkDate(Parser.parseDate(input), ui);
                        break;
                    case MARK: {
                        int index = Parser.parseTaskIndex(input, tasks.size());
                        mark(index, ui);
                        break;
                    }
                    case UNMARK: {
                        int index = Parser.parseTaskIndex(input, tasks.size());
                        unmark(index, ui);
                        break;
                    }
                    case DELETE: {
                        int index = Parser.parseTaskIndex(input, tasks.size());
                        deleteTask(index, ui);
                        break;
                    }
                    case TODO:
                    case DEADLINE:
                    case EVENT: {
                        Task newTask = Parser.parseTask(input, command);
                        addTask(newTask, ui);
                        break;
                    }
                }
            } catch (CharlieException e) {
                ui.showMessage(e.getMessage());
            }
            ui.showHorizontalLine();
        }
    }

    private static void mark(int index, Ui ui) {
        Task curTask = tasks.get(index);
        curTask.markDone();
        STORAGE.save(tasks.asList());
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + curTask.toString());
    }

    private static void unmark(int index, Ui ui) {
        Task curTask = tasks.get(index);
        curTask.markUndone();
        STORAGE.save(tasks.asList());
        ui.showMessage("OK, I've marked this task not done yet:");
        ui.showMessage("  " + curTask.toString());
    }

    private static void listTask(Ui ui) {
        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints deadlines and events that occur on the specified date.
     * Deadlines must match the date exactly, while events may span the date.
     *
     * @param searchDate date to check
     * @param ui user interface used to display matching tasks
     */
    private static void checkDate(LocalDate searchDate, Ui ui) {
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

    private static void addTask(Task task, Ui ui) {
        tasks.add(task);
        STORAGE.save(tasks.asList());
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task.toString());
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private static void deleteTask(int index, Ui ui) {
        Task deletedTask = tasks.remove(index);
        STORAGE.save(tasks.asList());
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + deletedTask.toString());
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

}
