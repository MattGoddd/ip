# Charlie User Guide

Charlie is a task chatbot. Tell Charlie what to remember, check what is coming up, and tick off tasks as you finish them.

## Getting started

You need Java 25. From the project folder, run `gradlew.bat run` on Windows or `./gradlew run` on macOS or Linux. Type a command into the chat box and press **Enter** or click **Roar!**.

Charlie saves your tasks in `data/charlie.txt` in the folder you run it from, so they are there when you reopen the app. Enter one command at a time. Dates use `yyyy-MM-dd` (for example, `2026-09-20`); times use four digits on a 24-hour clock (for example, `1400`).

## Add tasks

| What you want to add | Command | Example |
| --- | --- | --- |
| A task without a date | `todo DESCRIPTION` | `todo borrow book` |
| A task due on a date | `deadline DESCRIPTION /by yyyy-MM-dd` | `deadline return book /by 2026-09-20` |
| An event with a start and end | `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm` | `event study group /from 2026-09-21 1400 /to 2026-09-21 1600` |

Descriptions cannot be empty or contain `|`. An event must end after it starts. Charlie will tell you if a command needs correcting.

## View and find tasks

| Command | What it does |
| --- | --- |
| `list` | Shows all tasks and their numbers. Use these numbers for the commands below. |
| `find book` | Finds tasks with `book` in the description, regardless of letter case. You can also search for a phrase. |
| `on 2026-09-21` | Shows deadlines due that day and events spanning that day. |

In a list, `[T]` means todo, `[D]` means deadline, and `[E]` means event. `[ ]` means unfinished; `[X]` means done. Search results have their own numbering, so run `list` to get the task number before changing a task.

## Mark and unmark tasks

Use the number shown by `list`. Enter `mark TASK_NUMBER` to mark a task as done (`[X]`), or `unmark TASK_NUMBER` to return it to unfinished (`[ ]`). For example, `mark 2` marks the second task as done.

## Update a task

To change one detail without losing the task's position or completion status, use `update TASK_NUMBER FIELD NEW_VALUE`:

| Field | Works for | Example |
| --- | --- | --- |
| `description` | Any task | `update 1 description borrow library book` |
| `deadline` | Deadline | `update 2 deadline 2026-09-22` |
| `from` | Event | `update 3 from 2026-09-21 1500` |
| `to` | Event | `update 3 to 2026-09-21 1700` |

Use the task number from `list`. When changing an event time, its end must still be after its start. Run `list` again to check the result.

## Delete a task

Enter `delete TASK_NUMBER` to remove a task from the list. For example, `delete 2` removes the second task. Deletion is permanent; run `list` first to check the task number.

## Leave Charlie

Enter `bye` to close the chat window. Your task changes are saved as you make them.
