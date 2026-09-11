package charlie.command;

import charlie.exception.CharlieException;

public enum UpdateField {
    /** Description of task */
    DESCRIPTION("description"),
    /** Deadline of task */
    DEADLINE("deadline"),
    /** Start time of task */
    FROM("from"),
    /** End time of task */
    TO("to");


    /** Keyword to identify the UpdateField */
    private final String keyword;

    UpdateField(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a updateField keyword entered by the user into its corresponding enum value.
     *
     * @param keyword updatefield keyword entered by the user.
     * @return The matching command.
     * @throws CharlieException If the keyword does not represent a supported command.
     */
    public static UpdateField parseKeyword(String keyword) {
        for (UpdateField updateField : values()) {
            if (updateField.keyword.equals(keyword)) {
                return updateField;
            }
        }
        throw new CharlieException("Oops, this is an invalid field");
    }
}
