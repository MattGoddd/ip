# UI Test Plan

## Startup-error sessions

Run each startup-error case in a separate fresh Charlie process before the main session below.

### UI-STARTUP-01 — Reject a saved task with missing fields

**Aim:** Verify that a corrupted saved task reports a friendly error instead of causing an array-index failure.

**Rationale:** A deadline requires its type, status, description, and deadline fields in the save file.

Before starting Charlie, create `data/charlie.txt` with exactly this line:

```text
D | Not done | missing deadline
```

Start Charlie, then enter:

```text
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Invalid number of fields in saved task.
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Goodbye! See you next time.
    ____________________________________________________________
```

### UI-STARTUP-02 — Handle a save-file read failure

**Aim:** Verify that an I/O failure while reading the save path reports a friendly error.

**Rationale:** The program should not expose an uncaught `IOException` or Java stack trace when saved tasks cannot be read.

Before starting Charlie, remove the previous test file and create a directory named `data/charlie.txt`. Start Charlie, then enter:

```text
bye
```

**Expected startup output after the banner and greeting:**

```text
Error loading saved tasks: Could not read the saved task file.
```

**Expected output after entering `bye`:**

```text
    ____________________________________________________________
    Goodbye! See you next time.
    ____________________________________________________________
```

After this case, remove the `data/charlie.txt` directory before preparing the main session fixture.

## Session setup

- Required Java version: Java 25
- Compile command: `javac -d out src/main/java/*.java`
- Run command: `java -cp out Charlie`
- Before starting Charlie, create `data/charlie.txt` with exactly these lines:

```text
T | Done | loaded todo
D | Not done | loaded deadline | Friday
E | Done | loaded event | Monday | Tuesday
```

- Run all test cases below in one fresh program session and in the listed order.
- Compare output exactly, except that CRLF and LF line endings are considered equivalent and trailing spaces at the end of a line are ignored.
- Each expected-output block starts with the horizontal line printed after entering the command and ends with that command's final horizontal line.
- Console input echo is part of the transcript but is not part of the expected application output.

## UI-LOAD-01 — List tasks loaded at startup

**Aim:** Verify that Charlie restores todos, deadlines, events, and their completion states from the save file.

**Rationale:** Listing all preloaded task types confirms that startup loading reconstructs each saved field correctly.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][X] loaded todo
    2.[D][ ] loaded deadline (by: Friday)
    3.[E][X] loaded event (from: Monday to: Tuesday)
    ____________________________________________________________
```

## UI-LOAD-02 — Remove the loaded event

**Aim:** Remove the preloaded event as part of returning to an empty list for the existing test sequence.

**Rationale:** This also verifies that a task reconstructed from disk behaves like a newly created task.

**Input:**

```text
delete 3
```

**Expected output:**

```text
    ____________________________________________________________
    Noted. I've removed this task:
      [E][X] loaded event (from: Monday to: Tuesday)
    Now you have 2 tasks in the list.
    ____________________________________________________________
```

## UI-LOAD-03 — Remove the loaded deadline

**Aim:** Remove the preloaded deadline before running the original empty-list test sequence.

**Rationale:** Deleting a loaded deadline checks that its list position and stored data are valid.

**Input:**

```text
delete 2
```

**Expected output:**

```text
    ____________________________________________________________
    Noted. I've removed this task:
      [D][ ] loaded deadline (by: Friday)
    Now you have 1 tasks in the list.
    ____________________________________________________________
```

## UI-LOAD-04 — Remove the loaded todo

**Aim:** Return to an empty task list before running the original test sequence.

**Rationale:** Removing the final loaded task also verifies that saving an empty task list clears the save file.

**Input:**

```text
delete 1
```

**Expected output:**

```text
    ____________________________________________________________
    Noted. I've removed this task:
      [T][X] loaded todo
    Now you have 0 tasks in the list.
    ____________________________________________________________
```

## UI-01 — Add a todo

**Aim:** Verify that `todo` creates an incomplete todo with the correct description and confirmation.

**Rationale:** This is the simplest task-creation command, so it establishes that task descriptions, status symbols, and task counts are displayed correctly.

**Input:**

```text
todo borrow book
```

**Expected output:**

```text
    ____________________________________________________________
    Got it. I've added this task:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
    ____________________________________________________________
```

## UI-02 — Add a deadline

**Aim:** Verify that `/by` separates the deadline description from its date or time.

**Rationale:** A deadline contains two user-provided fields, making this case necessary to check both delimiter parsing and deadline formatting.

**Input:**

```text
deadline return book /by Sunday
```

**Expected output:**

```text
    ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Sunday)
    Now you have 2 tasks in the list.
    ____________________________________________________________
```

## UI-03 — Add an event

**Aim:** Verify that `/from` and `/to` separate the event description, start, and end.

**Rationale:** An event uses two delimiters and three fields, so it checks the most complex task parser and its output order.

**Input:**

```text
event project meeting /from Mon 2pm /to 4pm
```

**Expected output:**

```text
    ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 3 tasks in the list.
    ____________________________________________________________
```

## UI-04 — List all tasks

**Aim:** Verify that `list` displays every task in insertion order with one-based numbering.

**Rationale:** This case uses the state created by the preceding cases to check that different task types are retained and displayed together correctly.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sunday)
    3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
```

## UI-05 — Reject an invalid command

**Aim:** Verify that an unknown command displays a helpful error without terminating Charlie.

**Rationale:** Custom exceptions should be handled inside the input loop so the chatbot remains available after invalid input.

**Input:**

```text
hello
```

**Expected output:**

```text
    ____________________________________________________________
    Oops, this is an invalid command
    ____________________________________________________________
```

## UI-06 — Continue after an invalid command

**Aim:** Verify that Charlie still accepts commands after handling an invalid command.

**Rationale:** Printing an error is insufficient if the exception causes the input loop to end.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sunday)
    3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
    ____________________________________________________________
```

## UI-07 — Mark a task as done

**Aim:** Verify that `mark` changes the selected task's status to done and displays the updated task.

**Rationale:** Marking changes existing task state, so the response must identify the selected task and show its completed status symbol.

**Input:**

```text
mark 2
```

**Expected output:**

```text
    ____________________________________________________________
    Nice! I've marked this task as done:
      [D][X] return book (by: Sunday)
    ____________________________________________________________
```

## UI-08 — Unmark a task

**Aim:** Verify that `unmark` changes the selected task's status back to not done and displays the updated task.

**Rationale:** Unmarking is the inverse state transition and must restore the incomplete status symbol for the same task.

**Input:**

```text
unmark 2
```

**Expected output:**

```text
    ____________________________________________________________
    OK, I've marked this task not done yet:
      [D][ ] return book (by: Sunday)
    ____________________________________________________________
```

## UI-09 — Delete the third task

**Aim:** Verify that `delete 3` removes the selected event and reports the reduced task count.

**Rationale:** This directly checks the successful deletion behavior supplied by the user, including one-based indexing and the deleted task's exact representation.

**Input:**

```text
delete 3
```

**Expected output:**

```text
    ____________________________________________________________
    Noted. I've removed this task:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 2 tasks in the list.
    ____________________________________________________________
```

## UI-10 — List tasks after deletion

**Aim:** Verify that the deleted task is absent and the remaining tasks keep consecutive one-based numbering.

**Rationale:** A confirmation alone does not prove that deletion updated the stored list correctly.

**Input:**

```text
list
```

**Expected output:**

```text
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sunday)
    ____________________________________________________________
```

## UI-11 — Reject an empty command

**Aim:** Verify that submitting an empty line displays a helpful error.

**Rationale:** Empty input must be handled without terminating Charlie or producing inconsistent response separators.

**Input:** Submit one empty line.

```text

```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a command.
    ____________________________________________________________
```

## UI-12 — Reject a todo without a description

**Aim:** Verify that `todo` requires a description.

**Rationale:** A task without a description cannot convey useful work to the user.

**Input:**

```text
todo
```

**Expected output:**

```text
    ____________________________________________________________
    The task description cannot be empty.
    ____________________________________________________________
```

## UI-13 — Reject a deadline without `/by`

**Aim:** Verify that a deadline requires its `/by` delimiter.

**Rationale:** Without `/by`, Charlie cannot separate the task description from its deadline.

**Input:**

```text
deadline return book
```

**Expected output:**

```text
    ____________________________________________________________
    A deadline must include /by followed by a date.
    ____________________________________________________________
```

## UI-14 — Reject a deadline without a description

**Aim:** Verify that a deadline requires text before `/by`.

**Rationale:** Supplying a date alone does not describe a task.

**Input:**

```text
deadline /by Sunday
```

**Expected output:**

```text
    ____________________________________________________________
    Description cannot be empty.
    ____________________________________________________________
```

## UI-15 — Reject a deadline without a date

**Aim:** Verify that a deadline requires text after `/by`.

**Rationale:** An empty deadline value would create an incomplete Deadline object.

**Input:**

```text
deadline return book /by
```

**Expected output:**

```text
    ____________________________________________________________
    Deadline cannot be empty.
    ____________________________________________________________
```

## UI-16 — Reject an event without a start

**Aim:** Verify that an event requires text between `/from` and `/to`.

**Rationale:** An event with no starting value is incomplete.

**Input:**

```text
event meeting /from /to Friday
```

**Expected output:**

```text
    ____________________________________________________________
    from/to fields cannot be empty.
    ____________________________________________________________
```

## UI-17 — Reject an event without an end

**Aim:** Verify that an event requires text after `/to`.

**Rationale:** An event with no ending value is incomplete.

**Input:**

```text
event meeting /from Monday /to
```

**Expected output:**

```text
    ____________________________________________________________
    from/to fields cannot be empty.
    ____________________________________________________________
```

## UI-18 — Reject `mark` without a task number

**Aim:** Verify that `mark` requires exactly one task number.

**Rationale:** Charlie cannot identify which task to update when the number is absent.

**Input:**

```text
mark
```

**Expected output:**

```text
    ____________________________________________________________
    Please provide exactly one task number.
    ____________________________________________________________
```

## UI-19 — Reject a non-numeric task number

**Aim:** Verify that `mark` rejects text in place of a number.

**Rationale:** Number parsing failures should become friendly CharlieException messages rather than terminate the program.

**Input:**

```text
mark abc
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a valid task number.
    ____________________________________________________________
```

## UI-20 — Reject an out-of-range task number

**Aim:** Verify that `mark` rejects a number outside the current task list.

**Rationale:** Preventing invalid array access avoids crashes and accidental updates.

**Input:**

```text
mark 999
```

**Expected output:**

```text
    ____________________________________________________________
    Please enter a task number from 1 to 2.
    ____________________________________________________________
```

## UI-21 — Exit after persistence checks

**Aim:** End the test session normally and verify the final persisted task list.

**Rationale:** The final file contents confirm that additions, status changes, and deletion have all been written to disk.

**Input:**

```text
bye
```

**Expected output:**

```text
    ____________________________________________________________
    Goodbye! See you next time.
    ____________________________________________________________
```

**Expected `data/charlie.txt` contents after exit:**

```text
T | Not done | borrow book
D | Not done | return book | Sunday
```
