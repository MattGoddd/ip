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
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                String[] parts = input.trim().split("\\s+");
                String command = parts[0];
                horizontalLine();
                if (command.equals("bye")) {
                    outro();
                    break;
                } else if (command.equals("list")) {
                    for (int i = 0; i < taskCount; i++) {
                        printBotLine((i + 1) + "." + TASKS[i]);
                    }
                } else if (command.equals("mark")) {
                    int index = Integer.parseInt(parts[1]) - 1;
                    mark(index);
                } else if (command.equals("unmark")) {
                    int index = Integer.parseInt(parts[1]) - 1;
                    unmark(index);
                } else {
                    Task newTask = new Task(input, false);
                    TASKS[taskCount] = newTask;
                    taskCount++;
                    printBotLine("Got it. I've added this task: \n"
                            + newTask.toString() + "\n"
                            + "Now you have" + taskCount + "tasks in the list.");
                }
                horizontalLine();
            }
        }
    }

    private static void mark(int index) {
        Task curTask = TASKS[index];
        curTask.markDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + curTask.toString());
    }

    private static void unmark(int index) {
        Task curTask = TASKS[index];
        curTask.markUndone();
        System.out.println("OK, I've marked this task not done yet: \n  " + curTask.toString());
    }

}
