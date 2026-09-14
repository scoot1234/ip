# Orbit User Guide

Orbit is a mission-control inspired desktop task companion. It helps you track to-dos, deadlines, and events in
one place, with a priority for every mission.

## Quick start

1. Open a terminal in the project folder.
2. Run `./gradlew run` in Git Bash, or `.\gradlew.bat run` in PowerShell.
3. Type a command in Orbit's command field, then press Enter or select **Launch**.

Orbit saves task data automatically, so your missions remain available the next time you open the application.

## Using Orbit

- Your commands appear in indigo cards on the right.
- Orbit's mission-control messages appear in slate cards on the left.
- A pink card means Orbit needs a course correction: read the message and adjust the command.
- The **Tasks** panel lists current tasks. Drag the divider or resize the window to give the conversation or
  panel more room.

## Features

### Add a to-do

Adds a task without a date or time.

Format: `todo DESCRIPTION [/priority low|med|high]`

Example: `todo revise lecture notes /priority high`

### Add a deadline

Adds a task due on a specified date.

Format: `deadline DESCRIPTION /by YYYY-MM-DD [/priority low|med|high]`

Example: `deadline submit report /by 2026-10-03 /priority high`

### Add an event

Adds an event with start and end date-times. Use a 24-hour clock.

Format: `event DESCRIPTION /from YYYY-MM-DD HH:MM /to YYYY-MM-DD HH:MM [/priority low|med|high]`

Example: `event project meeting /from 2026-10-01 14:00 /to 2026-10-01 15:30 /priority med`

### Priorities

Every new task has a priority. If you omit `/priority`, Orbit assigns `low`. Valid values are `low`, `med`, and
`high`; uppercase and mixed-case values also work. Place `/priority` after the date fields for deadlines and events.

### List missions

Shows all saved tasks, including their completion status and priority.

Format: `list`

### Find missions

Finds tasks whose description contains the keyword, or tasks with an exactly matching priority.

Format: `find KEYWORD`

Examples: `find project` and `find high`

Description searches are case-sensitive. Priority searches are case-insensitive.

### Mark or unmark a mission

Marks a task as complete or restores it to the active state. Task numbers start at 1.

Formats: `mark TASK_NUMBER` and `unmark TASK_NUMBER`

Examples: `mark 2` and `unmark 2`

### Delete a mission

Permanently removes the selected task.

Format: `delete TASK_NUMBER`

Example: `delete 3`

### Exit Orbit

Closes the application.

Format: `bye`

## Tips

- Commands are lowercase and must begin the input.
- Check task numbers with `list` before marking, unmarking, or deleting a mission.
- If a command is invalid, no task is changed. Use Orbit's pink error card to correct the command and try again.

## Acknowledgements

OpenAI Codex was used throughout this individual project to assist with design, implementation, testing, and
documentation. The project owner reviewed and integrated the generated suggestions.
