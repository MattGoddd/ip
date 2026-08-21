/**
 * Starts the CHARLIE chatbot application.
 */
public class Charlie {
    private static final String BOT_NAME = "Charlie";
    public static void main(String[] args) {
        String banner = "  ____ _   _    _    ____  _     ___ _____\n"
                + " / ___| | | |  / \\  |  _ \\| |   |_ _| ____|\n"
                + "| |   | |_| | / _ \\ | |_) | |    | ||  _|\n"
                + "| |___|  _  |/ ___ \\|  _ <| |___ | || |___\n"
                + " \\____|_| |_/_/   \\_\\_| \\_\\_____|___|_____|\n";
        intro(BOT_NAME, banner);
        outro();
    }

    private static void horizontalLine() {
        String horizontalLine = "____________________________________________________________";
        System.out.println(horizontalLine);
    }

    private static void intro(String name, String banner) {
        horizontalLine();
        System.out.println(banner);
        System.out.println("Hello! I'm " + name + "!");
        System.out.println("What do you want to do today?");
        horizontalLine();
    }

    private static void outro() {
        System.out.println("Goodbye! See you next time.");
        horizontalLine();
    }
}
