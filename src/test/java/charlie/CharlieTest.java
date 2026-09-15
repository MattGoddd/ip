package charlie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CharlieTest {
    private static final String GREETING =
            "Hello! I'm Charlie!\nWhat do you want to do today?";

    @Test
    public void getGreeting_missingSaveFile_returnsGreeting(@TempDir Path temporaryDirectory) {
        Charlie charlie = new Charlie(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(GREETING, charlie.getGreeting());
    }

    @Test
    public void getGreeting_invalidSaveFile_returnsGreetingWithLoadingError(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, "D | Not done | missing date");
        Charlie charlie = new Charlie(saveFile.toString());

        assertEquals(
                GREETING + "\nError loading saved tasks: Invalid number of fields in saved task.",
                charlie.getGreeting());
        assertEquals("Here are the tasks in your list:", charlie.getResponse("list"));
    }

    @Test
    public void getResponse_sequenceOfCommands_updatesAndPersistsTasks(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Charlie charlie = new Charlie(saveFile.toString());

        assertEquals(
                "Got it. I've added this task:" + System.lineSeparator()
                        + "  [T][ ] borrow book" + System.lineSeparator()
                        + "Now you have 1 tasks in the list.",
                charlie.getResponse("todo borrow book"));
        assertEquals(
                "Nice! I've marked this task as done:" + System.lineSeparator()
                        + "  [T][X] borrow book",
                charlie.getResponse("mark 1"));
        assertEquals("T | Done | borrow book" + System.lineSeparator(),
                Files.readString(saveFile));
    }

    @Test
    public void getResponse_invalidThenExit_updatesExitSignal(@TempDir Path temporaryDirectory) {
        Charlie charlie = new Charlie(temporaryDirectory.resolve("charlie.txt").toString());

        assertEquals("Goodbye! See you next time.", charlie.getResponse("bye"));
        assertTrue(charlie.isExitRequested());

        assertEquals("Oops, this is an invalid command", charlie.getResponse("hello"));
        assertFalse(charlie.isExitRequested());
    }

    @Test
    public void run_listAndExit_processesConsoleCommands(@TempDir Path temporaryDirectory) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        String commands = "list" + System.lineSeparator() + "bye" + System.lineSeparator();
        ByteArrayInputStream testInput = new ByteArrayInputStream(
                commands.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();

        try {
            System.setIn(testInput);
            System.setOut(new PrintStream(testOutput, true, StandardCharsets.UTF_8));
            Charlie charlie = new Charlie(temporaryDirectory.resolve("charlie.txt").toString());

            charlie.run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm Charlie!"));
        assertTrue(output.contains("Here are the tasks in your list:"));
        assertTrue(output.contains("Goodbye! See you next time."));
    }
}
