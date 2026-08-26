import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private static final String BOT_NAME = "Charlie";
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String INDENT = "    ";
    private static final ArrayList<Task> TASKS = new ArrayList<>();
    private static int taskCount = 0;
    private static final Path SAVE_PATH = Path.of("data", "charlie.txt");

    public static void main(String[] args) {
        String banner = "  ____ _   _    _    ____  _     ___ _____\n"
                + " / ___| | | |  / \\  |  _ \\| |   |_ _| ____|\n"
                + "| |   | |_| | / _ \\ | |_) | |    | ||  _|\n"
                + "| |___|  _  |/ ___ \\|  _ <| |___ | || |___\n"
                + " \\____|_| |_/_/   \\_\\_| \\_\\_____|___|_____|\n";
        intro(banner);
        try {
            loadTasks();
        } catch (CharlieException e) {
            System.out.println("Error loading saved tasks: " + e.getMessage());
        }
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
                    horizontalLine();
                    Command command = Command.fromKeyword(parts[0]);
                    switch (command) {
                        case BYE:
                            outro();
                            break label;
                        case LIST:
                            listTask();
                            break;
                        case MARK: {
                            int index = parseTaskIndex(parts);
                            mark(index);
                            break;
                        }
                        case UNMARK: {
                            int index = parseTaskIndex(parts);
                            unmark(index);
                            break;
                        }
                        case DELETE: {
                            int index = parseTaskIndex(parts);
                            deleteTask(index);
                            break;
                        }
                        case TODO:
                        case DEADLINE:
                        case EVENT: {
                            Task newTask = parseTask(input, command);
                            addTask(newTask);
                            break;
                        }
                    }
                } catch (CharlieException e) {
                    printBotLine(e.getMessage());
                }
                horizontalLine();
            }
        }
    }

    private static void mark(int index) {
        Task curTask = TASKS.get(index);
        curTask.markDone();
        save();
        printBotLine("Nice! I've marked this task as done:");
        printBotLine("  " + curTask.toString());
    }

    private static void unmark(int index) {
        Task curTask = TASKS.get(index);
        curTask.markUndone();
        save();
        printBotLine("OK, I've marked this task not done yet:");
        printBotLine("  " + curTask.toString());
    }

    private static void listTask() {
        printBotLine("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            printBotLine((i + 1) + "." + TASKS.get(i));
        }
    }

    /**
     * Converts a task command into the corresponding type of task.
     * Throws a {@link CharlieException} when the command does not have the expected format.
     *
     * @param input complete user input containing the task details
     * @param command type of task to create
     * @return a task containing the parsed details
     */
    private static Task parseTask(String input, Command command) {
        String[] commandAndArguments = input.trim().split("\\s+", 2);
        if (commandAndArguments.length < 2) {
            throw new CharlieException("The task description cannot be empty.");
        }

        String arguments = commandAndArguments[1];

        if (command == Command.TODO) {
            return new Todo(arguments, false);
        } else if (command == Command.DEADLINE) {
            int byPosition = arguments.indexOf("/by");
            if (byPosition == -1) {
                throw new CharlieException("A deadline must include /by followed by a date.");
            }
            String description = arguments.substring(0, byPosition).trim();
            if (description.isEmpty()) {
                throw new CharlieException("Description cannot be empty.");
            }
            String deadline = arguments.substring(byPosition + "/by".length()).trim();
            if (deadline.isBlank()) {
                throw new CharlieException("Deadline cannot be empty.");
            }
            return new Deadline(description, false, deadline);
        } else { // readCommand only passes EVENT as the remaining command type.
            int fromPosition = arguments.indexOf("/from");
            int toPosition = arguments.indexOf("/to");
            if (fromPosition == -1 || toPosition == -1) {
                throw new CharlieException("Need to include /from or /to fields.");
            } else if (fromPosition > toPosition) {
                throw new CharlieException("Invalid argument format: /from should appear before /to");
            }

            String description = arguments.substring(0, fromPosition).trim();
            if (description.isEmpty()) {
                throw new CharlieException("Description cannot be empty");
            }
            String from = arguments.substring(fromPosition + "/from".length(), toPosition).trim();
            String to = arguments.substring(toPosition + "/to".length()).trim();
            if (from.isBlank() || to.isBlank()) {
                throw new CharlieException("from/to fields cannot be empty.");
            }
            return new Event(description, false, from, to);
        }
    }

    private static void addTask(Task task) {
        TASKS.add(task);
        taskCount++;
        save();
        printBotLine("Got it. I've added this task:");
        printBotLine("  " + task.toString());
        printBotLine("Now you have " + taskCount + " tasks in the list.");
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
            throw new CharlieException("There are no tasks in the list.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new CharlieException(
                    "Please enter a task number from 1 to " + taskCount + ".");
        }

        return taskNumber - 1;
    }

    private static void deleteTask(int index) {
        Task deletedTask = TASKS.remove(index);
        taskCount--;
        save();
        printBotLine("Noted. I've removed this task:");
        printBotLine("  " + deletedTask.toString());
        printBotLine("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     *  Saves the list details whenever a command is run into charlie.txt
     *
     */
    private static void save() {
        StringBuilder content = new StringBuilder();
        for (Task task : TASKS) {
            content.append(task.saveFileFormat()).append(System.lineSeparator());
        }
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            Files.writeString(SAVE_PATH, content.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not save tasks.", e);
        }
    }

    /**
     * Loads all saved tasks into memory when Charlie starts.
     * A missing save file represents a user who does not have any saved tasks yet.
     */
    private static void loadTasks() {
        // Instance when the file is not created, then we return an empty List to TASKS
        if (!Files.exists(SAVE_PATH)) {
            return;
        }

        try {
            List<String> savedLines = Files.readAllLines(SAVE_PATH);
            for (String line : savedLines) {
                if (!line.isBlank()) {
                    TASKS.add(parseSavedTask(line));
                }
            }
            taskCount = TASKS.size();
        } catch (IOException e) {
            throw new CharlieException("Could not read the saved task file.");
        }
    }

    /**
     * Recreates one task from a line in Charlie's save-file format.
     *
     * @param line saved representation of one task
     * @return the reconstructed task
     */
    private static Task parseSavedTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        int expectedFieldCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new CharlieException("Unknown saved task type: " + fields[0]);
        };
        if (fields.length != expectedFieldCount) {
            throw new CharlieException("Invalid number of fields in saved task.");
        }

        boolean isDone = fields[1].equals("Done");

        return switch (fields[0]) {
            case "T" -> new Todo(fields[2], isDone);
            case "D" -> new Deadline(fields[2], isDone, fields[3]);
            case "E" -> new Event(fields[2], isDone, fields[3], fields[4]);
            default -> throw new AssertionError("Task type was validated above.");
        };
    }
}
