import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private static final ArrayList<Task> TASKS = new ArrayList<>();
    private static final Storage STORAGE = new Storage("data/charlie.txt");
    private static int taskCount = 0;

    public static void main(String[] args) {
        try (Ui ui = new Ui()) {
            ui.showIntro();
            try {
                TASKS.addAll(STORAGE.load());
                taskCount = TASKS.size();
            } catch (CharlieException e) {
                ui.showLoadingError(e.getMessage());
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
                        int index = Parser.parseTaskIndex(input, taskCount);
                        mark(index, ui);
                        break;
                    }
                    case UNMARK: {
                        int index = Parser.parseTaskIndex(input, taskCount);
                        unmark(index, ui);
                        break;
                    }
                    case DELETE: {
                        int index = Parser.parseTaskIndex(input, taskCount);
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
        Task curTask = TASKS.get(index);
        curTask.markDone();
        STORAGE.save(TASKS);
        ui.showMessage("Nice! I've marked this task as done:");
        ui.showMessage("  " + curTask.toString());
    }

    private static void unmark(int index, Ui ui) {
        Task curTask = TASKS.get(index);
        curTask.markUndone();
        STORAGE.save(TASKS);
        ui.showMessage("OK, I've marked this task not done yet:");
        ui.showMessage("  " + curTask.toString());
    }

    private static void listTask(Ui ui) {
        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            ui.showMessage((i + 1) + "." + TASKS.get(i));
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
        int matchCount = 0;
        ui.showMessage("Here are the tasks occurring on " + searchDate + ":");

        for (Task task : TASKS) {
            boolean matches = false;

            if (task instanceof Deadline deadlineTask) {
                matches = deadlineTask.deadline.equals(searchDate);
            } else if (task instanceof Event eventTask) {
                LocalDate fromDate = eventTask.from.toLocalDate();
                LocalDate toDate = eventTask.to.toLocalDate();

                matches = !searchDate.isBefore(fromDate) && !searchDate.isAfter(toDate);
            }

            if (matches) {
                matchCount++;
                ui.showMessage(matchCount + "." + task);
            }
        }

        if (matchCount == 0) {
            ui.showMessage("No deadlines or events occur on this date.");
        }
    }

    private static void addTask(Task task, Ui ui) {
        TASKS.add(task);
        taskCount++;
        STORAGE.save(TASKS);
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task.toString());
        ui.showMessage("Now you have " + taskCount + " tasks in the list.");
    }

    private static void deleteTask(int index, Ui ui) {
        Task deletedTask = TASKS.remove(index);
        taskCount--;
        STORAGE.save(TASKS);
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + deletedTask.toString());
        ui.showMessage("Now you have " + taskCount + " tasks in the list.");
    }

}
