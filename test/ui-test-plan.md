# UI Test Plan

Use the exact template below for every test case. Keep the expected output as the complete console session, including the greeting and divider lines.

## Test Case 1: Add and list each task type

Aim: Verify that todo, deadline, and event commands create the correct task types, save each task-list change, and that `list` shows their statuses and details.

### Inputs

```text
todo borrow book
deadline return book /by 2026-01-18
event project meeting /from 2026-01-18 14:00 /to 2026-01-18 16:00
mark 1
list
find 2026-01-18
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Hello! I'm Bloop.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jan 18 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Jan 18 2026 14:00 to: Jan 18 2026 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] borrow book
 2.[D][ ] return book (by: Jan 18 2026)
 3.[E][ ] project meeting (from: Jan 18 2026 14:00 to: Jan 18 2026 16:00)
____________________________________________________________
____________________________________________________________
 Here are the tasks on 2026-01-18:
 2.[D][ ] return book (by: Jan 18 2026)
 3.[E][ ] project meeting (from: Jan 18 2026 14:00 to: Jan 18 2026 16:00)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 5: Load saved tasks at startup

Aim: Verify that a new chatbot session reads saved todo, deadline, and event tasks and restores their completion statuses.

### Initial Data

```text
T | 1 | read book
D | 0 | return book | 2026-01-16
E | 1 | project meeting | 2026-01-18T14:00 | 2026-01-18T16:00
```

### Inputs

```text
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Hello! I'm Bloop.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Jan 16 2026)
 3.[E][X] project meeting (from: Jan 18 2026 14:00 to: Jan 18 2026 16:00)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 2: Reject corrupt saved data

Aim: Verify that malformed saved data is reported as a user-facing error instead of causing a crash.

### Initial Data

```text
D | 2 | return book | 2026-01-16
```

### Inputs

```text
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Hello! I'm Bloop.
What can I do for you?
____________________________________________________________
 OOPS!!! Your saved task data is invalid.
____________________________________________________________
```

## Test Case 3: Reject invalid input without changing tasks

Aim: Verify that invalid commands and malformed task details display specific errors while valid commands before and after them retain the correct task-list state.

### Inputs

```text
todo
todo first task
blah
deadline submit report
deadline submit report /by 2026-01-16
event team meeting /from 2026-01-16 14:00
event /from 2026-01-16 14:00 /to 2026-01-16 16:00
event team meeting /from 2026-01-16 14:00 /to 2026-01-16 16:00
mark 7
mark two
mark 2
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Hello! I'm Bloop.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 OOPS!!! Use deadline <description> /by <date/time>.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Jan 16 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Use event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of an event cannot be empty.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team meeting (from: Jan 16 2026 14:00 to: Jan 16 2026 16:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must refer to an existing task.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] submit report (by: Jan 16 2026)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first task
 2.[D][X] submit report (by: Jan 16 2026)
 3.[E][ ] team meeting (from: Jan 16 2026 14:00 to: Jan 16 2026 16:00)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 3: Delete a task and preserve remaining tasks

Aim: Verify that `delete` removes the selected task, shifts later tasks into the correct list position, and rejects missing or invalid task numbers without changing the list.

### Inputs

```text
todo read book
deadline return book /by 2026-01-16
event meeting /from 2026-01-16 09:00 /to 2026-01-17 10:00
mark 2
delete
delete 4
delete 2
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Hello! I'm Bloop.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jan 16 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: Jan 16 2026 09:00 to: Jan 17 2026 10:00)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jan 16 2026)
____________________________________________________________
____________________________________________________________
 OOPS!!! Please specify a task number to delete.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must refer to an existing task.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] return book (by: Jan 16 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] meeting (from: Jan 16 2026 09:00 to: Jan 17 2026 10:00)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 4: Reverse a completed task

Aim: Verify that `unmark` reverses a completed task and that `list` retains the reversed status.

### Inputs

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____  _
| __ )| | ___   ___  _ __
|  _ \| |/ _ \ / _ \| '_ \
| |_) | | (_) | (_) | |_) |
|____/|_|\___/ \___/| .__/
                    |_|
Hello! I'm Bloop.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
