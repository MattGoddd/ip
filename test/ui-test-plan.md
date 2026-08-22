# UI Test Plan

## Session setup

- Required Java version: Java 25
- Compile command: `javac -d out src/main/java/*.java`
- Run command: `java -cp out Charlie`
- Run all test cases below in one fresh program session and in the listed order.
- Compare output exactly, except that CRLF and LF line endings are considered equivalent.
- Each expected-output block starts with the horizontal line printed after entering the command and ends with that command's final horizontal line.
- Console input echo is part of the transcript but is not part of the expected application output.

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
