package charlie.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.Todo;

/**
 * Loads tasks from a save file and writes the current task list back to it.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the specified save file.
     *
     * @param filePath Path to Charlie's save file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all tasks from the save file.
     * A missing file represents a first-time user with no saved tasks.
     *
     * @return Tasks reconstructed from the save file.
     * @throws CharlieException If the file cannot be read or contains invalid task data.
     */
    public List<Task> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    tasks.add(parseSavedTask(line));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new CharlieException("Could not read the saved task file.");
        }
    }

    /**
     * Saves every task using Charlie's line-based file format.
     *
     * @param tasks Tasks to save.
     * @throws CharlieException If the tasks cannot be written to the save file.
     */
    public void save(List<Task> tasks) {
        StringBuilder content = new StringBuilder();
        for (Task task : tasks) {
            content.append(task.saveFileFormat()).append(System.lineSeparator());
        }
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content.toString());
        } catch (IOException e) {
            throw new CharlieException("Could not save tasks.");
        }
    }

    /**
     * Recreates one task from a line in Charlie's save-file format.
     *
     * @param line Saved representation of one task.
     * @return The reconstructed task.
     */
    private Task parseSavedTask(String line) {
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

        try {
            return switch (fields[0]) {
                case "T" -> new Todo(fields[2], isDone);
                case "D" -> new Deadline(fields[2], isDone, LocalDate.parse(fields[3]));
                case "E" -> {
                    try {
                        yield new Event(fields[2], isDone,
                                LocalDateTime.parse(fields[3]),
                                LocalDateTime.parse(fields[4]));
                    } catch (DateTimeParseException e) {
                        throw new CharlieException("Saved event contains an invalid date-time.");
                    }
                }
                default -> throw new AssertionError("Task type was validated above.");
            };
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Deadline must be a valid date in yyyy-MM-dd format.");
        }
    }
}
