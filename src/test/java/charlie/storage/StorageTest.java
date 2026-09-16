package charlie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.Todo;

public class StorageTest {
    @Test
    public void load_missingFile_returnsEmptyList(@TempDir Path temporaryDirectory) {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesTaskData(
            @TempDir Path temporaryDirectory) {
        Path saveFile = temporaryDirectory.resolve("nested").resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<Task> tasks = List.of(
                new Todo("borrow book", true),
                new Deadline("submit report", false, LocalDate.of(2026, 9, 20)),
                new Event(
                        "project meeting",
                        true,
                        LocalDateTime.of(2026, 9, 21, 14, 0),
                        LocalDateTime.of(2026, 9, 21, 16, 0)));

        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(tasks.stream().map(Task::saveFileFormat).toList(),
                loadedTasks.stream().map(Task::saveFileFormat).toList());
    }

    @Test
    public void load_blankLines_ignoresBlankLines(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, System.lineSeparator()
                + "T | Not done | borrow book" + System.lineSeparator()
                + "   " + System.lineSeparator());

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] borrow book", tasks.get(0).toString());
    }

    @Test
    public void load_unknownTaskType_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(temporaryDirectory, "X | Not done | mystery");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Unknown saved task type: X", exception.getMessage());
    }

    @Test
    public void load_wrongFieldCount_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(temporaryDirectory, "D | Not done | missing deadline");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Invalid number of fields in saved task.", exception.getMessage());
    }

    @Test
    public void load_invalidDeadline_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(
                temporaryDirectory, "D | Not done | impossible | 2026-02-30");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Deadline must be a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void load_invalidEventDateTime_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path saveFile = writeSavedLine(
                temporaryDirectory,
                "E | Not done | meeting | invalid | 2026-09-21T16:00");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(saveFile.toString()).load());

        assertEquals("Saved event contains an invalid date-time.", exception.getMessage());
    }

    @Test
    public void load_pathIsDirectory_exceptionThrown(@TempDir Path temporaryDirectory) {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> new Storage(temporaryDirectory.toString()).load());

        assertEquals("Could not read the saved task file.", exception.getMessage());
    }

    @Test
    public void save_parentIsFile_exceptionThrown(@TempDir Path temporaryDirectory)
            throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "not a directory");
        Storage storage = new Storage(blockedParent.resolve("charlie.txt").toString());

        CharlieException exception = assertThrows(
                CharlieException.class, () -> storage.save(List.of(new Todo("unsaved", false))));

        assertEquals("Could not save tasks.", exception.getMessage());
    }

    private Path writeSavedLine(Path temporaryDirectory, String line) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, line + System.lineSeparator());
        return saveFile;
    }
}
