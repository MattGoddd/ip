package charlie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void mark_changedFromUndoneToDone_returnsDoneTask() {
        Todo todo = new Todo("sample", false);
        TaskList taskList = new TaskList(List.of(todo));
        assertTrue(taskList.mark(0).isDone);
    }

    @Test
    public void findOnDate_allTasksMatch_returnsAllTasks() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 10; i++) {
            taskList.add(new Deadline(
                    "sample " + i,
                    false,
                    LocalDate.of(2026, 1, 1)
            ));
        }
        assertEquals(10,
                taskList.findOnDate(
                        LocalDate.of(2026, 1, 1)).size()
        );
    }

    @Test
    public void findOnDate_noTasksMatch_returnsEmptyList() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 10; i++) {
            taskList.add(new Deadline(
                    "sample " + i,
                    false,
                    LocalDate.of(2026, 1, 1)
            ));
        }
        assertEquals(0,
                taskList.findOnDate(
                        LocalDate.of(2025, 1, 1)
                ).size());
    }

    @Test
    public void findOnDate_dateWithinEventRange_returnsEvent() {
        TaskList taskList = new TaskList(
                List.of(
                        new Event("sample",
                                false,
                                LocalDateTime.of(2026, 1, 1, 0, 0),
                                LocalDateTime.of(2026, 1, 10, 0, 0))
                )
        );
        assertEquals(1, taskList.findOnDate(
                LocalDate.of(2026, 1, 5)
        ).size());
    }

    @Test
    public void findByKeyword_multipleMatches_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("borrow book", false);
        Todo nonMatch = new Todo("submit report", false);
        Todo secondMatch = new Todo("return book", false);
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), taskList.findByKeyword("book"));
    }

    @Test
    public void findByKeyword_phraseMatches_returnsMatchingTask() {
        Todo matchingTask = new Todo("project meeting", false);
        TaskList taskList = new TaskList(List.of(
                new Todo("project report", false),
                matchingTask
        ));

        assertEquals(List.of(matchingTask), taskList.findByKeyword("project meeting"));
    }

    @Test
    public void findByKeyword_mixedCaseDescriptionAndKeyword_returnsMatchingTask() {
        Todo matchingTask = new Todo("boRRoW boOk", false);
        TaskList taskList = new TaskList(List.of(matchingTask));

        assertEquals(List.of(matchingTask), taskList.findByKeyword("BOOK"));
    }

    @Test
    public void findByKeyword_noMatch_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("borrow book", false)));

        assertTrue(taskList.findByKeyword("report").isEmpty());
    }
}
