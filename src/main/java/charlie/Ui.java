package charlie;

import java.util.Scanner;

/**
 * Handles console input and presents messages to Charlie's user.
 */
public class Ui implements AutoCloseable {
    private static final String BOT_BANNER = "  ____ _   _    _    ____  _     ___ _____\n"
            + " / ___| | | |  / \\  |  _ \\| |   |_ _| ____|\n"
            + "| |   | |_| | / _ \\ | |_) | |    | ||  _|\n"
            + "| |___|  _  |/ ___ \\|  _ <| |___ | || |___\n"
            + " \\____|_| |_/_/   \\_\\_| \\_\\_____|___|_____|\n";
    private static final String BOT_HORIZONTAL_LINE =
            "____________________________________________________________";
    private static final String BOT_INDENT = "    ";
    private static final String BOT_NAME = "Charlie";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return True when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command entered by the user.
     *
     * @return The next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Charlie's startup banner and greeting.
     */
    public void showIntro() {
        showHorizontalLine();
        for (String line : BOT_BANNER.split("\n")) {
            showMessage(line);
        }
        showMessage("Hello! I'm " + BOT_NAME + "!");
        showMessage("What do you want to do today?");
        showHorizontalLine();
    }

    /**
     * Displays Charlie's farewell message.
     */
    public void showOutro() {
        showMessage("Goodbye! See you next time.");
        showHorizontalLine();
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message Explanation of the loading problem.
     */
    public void showLoadingError(String message) {
        System.out.println("Error loading saved tasks: " + message);
    }

    /**
     * Displays one message using Charlie's standard indentation.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(BOT_INDENT + message);
    }

    /**
     * Displays the separator used to group each command and response.
     */
    public void showHorizontalLine() {
        showMessage(BOT_HORIZONTAL_LINE);
    }

    /**
     * Closes the standard-input scanner when Charlie exits.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
