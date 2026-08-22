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
