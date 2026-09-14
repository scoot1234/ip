# Bloop User Guide

Bloop manages to-do tasks, deadlines, and events. Each task has a priority: `low`, `med`, or `high`.
When omitted, priority defaults to `low`.

## Add a to-do

`todo <description> [/priority <low|med|high>]`

Examples: `todo read book` and `todo submit reflection /priority high`

## Add a deadline

`deadline <description> /by <yyyy-MM-dd> [/priority <low|med|high>]`

Example: `deadline return book /by 2026-09-10 /priority med`

## Add an event

`event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm> [/priority <low|med|high>]`

Example: `event team meeting /from 2026-09-10 09:00 /to 2026-09-10 10:00 /priority high`

Priority values are case-insensitive. For deadlines and events, `/priority` must appear after the date fields.

## Find tasks

`find <keyword>` finds tasks whose descriptions contain the keyword and tasks whose priority exactly matches it.
For example, `find high` finds every high-priority task.

## Priority display

```text
[T][ ] read book (priority: low)
[D][ ] return book (by: Sep 10 2026) (priority: high)
```
