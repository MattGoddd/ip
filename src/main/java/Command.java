/**
 * Represents a command that Charlie can recognise.
 */
public enum Command {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a command keyword entered by the user into its corresponding enum value.
     *
     * @param keyword command keyword entered by the user
     * @return the matching command
     * @throws CharlieException if the keyword does not represent a supported command
     */
    public static Command fromKeyword(String keyword) {
        for (Command command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        throw new CharlieException("Oops, this is an invalid command");
    }
}
