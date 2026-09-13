package charlie.command;

import charlie.exception.CharlieException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

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

    public void validateValueWithField(String value) {

        switch (this) {
            case DESCRIPTION -> { }
            case DEADLINE -> validateDeadline(value);
            case TO, FROM -> validateDateTime(value);
        }
    }

    private static void validateDeadline(String value) {
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Deadline must use the yyyy-MM-dd format.");
        }
    }

    private static void validateDateTime(String value) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Event dates must use the yyyy-MM-dd HHmm format.");
        }
    }
}
