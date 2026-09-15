package charlie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charlie.exception.CharlieException;

public class StorageTest {
    @Test
    public void load_invalidCompletionStatus_exceptionThrown(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, "T | Finished | borrow book");
        Storage storage = new Storage(saveFile.toString());

        CharlieException exception = assertThrows(CharlieException.class, storage::load);

        assertEquals("Invalid completion status in saved task.", exception.getMessage());
    }

    @Test
    public void load_blankDescription_exceptionThrown(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, "T | Not done | ");
        Storage storage = new Storage(saveFile.toString());

        CharlieException exception = assertThrows(CharlieException.class, storage::load);

        assertEquals("Description cannot be empty.", exception.getMessage());
    }

    @Test
    public void load_eventEndsBeforeStart_exceptionThrown(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(
                saveFile,
                "E | Not done | meeting | 2026-09-20T11:00 | 2026-09-20T10:00");
        Storage storage = new Storage(saveFile.toString());

        CharlieException exception = assertThrows(CharlieException.class, storage::load);

        assertEquals("Event end must be after its start.", exception.getMessage());
    }

}
