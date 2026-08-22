import java.util.Scanner;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private static final String BOT_NAME = "Charlie";
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String INDENT = "    ";
    private static final Task[] TASKS = new Task[100];
    private static int taskCount = 0;

    public static void main(String[] args) {
        String banner = "  ____ _   _    _    ____  _     ___ _____\n"
                + " / ___| | | |  / \\  |  _ \\| |   |_ _| ____|\n"
                + "| |   | |_| | / _ \\ | |_) | |    | ||  _|\n"
                + "| |___|  _  |/ ___ \\|  _ <| |___ | || |___\n"
                + " \\____|_| |_/_/   \\_\\_| \\_\\_____|___|_____|\n";
        intro(banner);
        readCommand();
    }

    private static void horizontalLine() {
        System.out.println(INDENT + HORIZONTAL_LINE);
    }

    private static void printBotLine(String message) {
        System.out.println(INDENT + message);
    }

    /**
     * Prints every row of the banner using the standard bot indentation.
     */
    private static void printBanner(String banner) {
        for (String line : banner.split("\n")) {
            printBotLine(line);
        }
    }

    private static void intro(String banner) {
        horizontalLine();
        printBanner(banner);
        printBotLine("Hello! I'm " + BOT_NAME + "!");
        printBotLine("What do you want to do today?");
        horizontalLine();
    }

    private static void outro() {
        printBotLine("Goodbye! See you next time.");
        horizontalLine();
    }

    /**
     * Reads commands until the user enters {@code bye}.
     * Other input is stored as a task, while {@code list} displays all stored tasks.
     */
    private static void readCommand() {
        try (Scanner scanner = new Scanner(System.in)) {
            label:
            while (scanner.hasNextLine()) {
                try {
                    String input = scanner.nextLine();
                    if (input.isBlank()) {
                        horizontalLine();
                        throw new CharlieException("Please enter a command.");
                    }
                    String[] parts = input.trim().split("\\s+");
                    String command = parts[0];
                    horizontalLine();
                    switch (command) {
                        case "bye":
                            outro();
                            break label;
                        case "list":
                            listTask();
                            break;
                        case "mark": {
                            int index = parseTaskIndex(parts);
                            mark(index);
                            break;
                        }
                        case "unmark": {
                            int index = parseTaskIndex(parts);
                            unmark(index);
                            break;
                        }
                        case "todo":
                        case "deadline":
                        case "event": {
                            Task newTask = parseTask(input);
                            addTask(newTask);
                            break;
                        }
                        default:
                            throw new CharlieException("Oops, this is an invalid command");
                    }
                } catch (CharlieException e) {
                    printBotLine(e.getMessage());
                }
                horizontalLine();
            }
        }
    }

    private static void mark(int index) {
        Task curTask = TASKS[index];
        curTask.markDone();
        printBotLine("Nice! I've marked this task as done:");
        printBotLine("  " + curTask.toString());
    }

    private static void unmark(int index) {
        Task curTask = TASKS[index];
        curTask.markUndone();
        printBotLine("OK, I've marked this task not done yet:");
        printBotLine("  " + curTask.toString());
    }

    private static void listTask() {
        printBotLine("Here are the tasks in your list: ");
        for (int i = 0; i < taskCount; i++) {
            printBotLine((i + 1) + "." + TASKS[i]);
        }
    }

    /**
     * Converts a task command into the corresponding type of task.
     * Throws a {@link CharlieException} when the command does not have the expected format.
     */
    private static Task parseTask(String input) {
        String[] commandAndArguments = input.trim().split("\\s+", 2);
        if (commandAndArguments.length < 2) {
            throw new CharlieException("The task description cannot be empty.");
        }

        String command = commandAndArguments[0];
        String arguments = commandAndArguments[1];

        if (command.equals("todo")) {
            return new Todo(arguments, false);
        } else if (command.equals("deadline")) {
            int byPosition = arguments.indexOf("/by");
            if (byPosition == -1) {
                throw new CharlieException("A deadline must include /by followed by a date.");
            }
            String description = arguments.substring(0, byPosition).trim();
            if (description.isEmpty()) {
                throw new CharlieException("Description cannot be empty");
            }
            String deadline = arguments.substring(byPosition + "/by".length()).trim();
            return new Deadline(description, false, deadline);
        } else { // Run else clause here due to the switch format ensuring this is "event"
            int fromPosition = arguments.indexOf("/from");
            int toPosition = arguments.indexOf("/to");
            if (fromPosition == -1 || toPosition == -1) {
                throw new CharlieException("from/to fields cannot be empty");
            } else if (fromPosition > toPosition) {
                throw new CharlieException("Invalid argument format");
            }

            String description = arguments.substring(0, fromPosition).trim();
            if (description.isEmpty()) {
                throw new CharlieException("Description cannot be empty");
            }
            String from = arguments.substring(fromPosition + "/from".length(), toPosition).trim();
            String to = arguments.substring(toPosition + "/to".length()).trim();
            return new Event(description, false, from, to);
        }
    }

    private static void addTask(Task task) {
        try {
            TASKS[taskCount] = task;
            taskCount++;
            printBotLine("Got it. I've added this task:");
            printBotLine("  " + task.toString());
            printBotLine("Now you have " + taskCount + " tasks in the list.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CharlieException("List can only hold 100 tasks");
        }
    }

    /**
     * Included this to separate parsing from readCommand()
     * Takes in the parsed String parts, and checks if the 2nd input is a valid int
     * Returns the index for mark / unmark
     * Throws a {@link CharlieException} with invalid input
     */
    private static int parseTaskIndex(String[] parts) {
        if (parts.length != 2) {
            throw new CharlieException("Please provide exactly one task number.");
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new CharlieException("Please enter a valid task number.");
        }

        if (taskCount <= 0) {
            throw new CharlieException("There are no tasks in the list");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new CharlieException(
                    "Please enter a task number from 1 to " + taskCount + ".");
        }

        return taskNumber - 1;
    }

}
