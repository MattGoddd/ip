package charlie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charlie.exception.CharlieException;
import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.task.Todo;
import charlie.ui.Ui;

public class UpdateCommandTest {
    @Test
    public void execute_validDescription_replacesPersistsAndDisplaysTask(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Todo("borrow book", false);
        TaskList tasks = new TaskList(List.of(originalTask));
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Storage storage = new Storage(saveFile.toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.DESCRIPTION, "borrow library book");

        command.execute(tasks, ui, storage);

        Task updatedTask = tasks.get(0);
        assertNotSame(originalTask, updatedTask);
        assertEquals("[T][ ] borrow library book", updatedTask.toString());
        assertEquals(
                "T | Not done | borrow library book" + System.lineSeparator(),
                Files.readString(saveFile));
        assertEquals(
                List.of("Updated this task:", "  [T][ ] borrow library book"),
                messages);
    }

    @Test
    public void execute_storageFailure_keepsOriginalTaskAndDisplaysNothing(
            @TempDir Path temporaryDirectory) throws IOException {
        Task originalTask = new Todo("borrow book", false);
        TaskList tasks = new TaskList(List.of(originalTask));
        Path blockedParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockedParent, "blocks directory creation");
        Storage storage = new Storage(blockedParent.resolve("charlie.txt").toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        UpdateCommand command = new UpdateCommand(0, UpdateField.DESCRIPTION, "borrow library book");

        CharlieException exception = assertThrows(
                CharlieException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("Could not save tasks.", exception.getMessage());
        assertSame(originalTask, tasks.get(0));
        assertTrue(messages.isEmpty());
    }
}
