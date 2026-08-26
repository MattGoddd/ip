/**
 * Represents an error that Charlie can explain to the user without terminating unexpectedly.
 */
public class CharlieException extends RuntimeException {
    public CharlieException(String message) {
        super(message);
    }
}
