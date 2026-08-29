package charlie.command;

import charlie.exception.CharlieException;

/**
 * Identifies a command that Charlie can recognize.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    ON("on"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a command keyword entered by the user into its corresponding enum value.
     *
     * @param keyword Command keyword entered by the user.
     * @return The matching command.
     * @throws CharlieException If the keyword does not represent a supported command.
     */
    public static CommandType parseKeyword(String keyword) {
        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(keyword)) {
                return commandType;
            }
        }
        throw new CharlieException("Oops, this is an invalid command");
    }
}
