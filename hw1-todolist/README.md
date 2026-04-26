# ToDoList App

## How the App Works
ToDoList is a simple task management app built with Kotlin and Android Studio.

**Features:**
- Add new tasks to the list using the input field and Add button
- Each task automatically prompts a date and time picker to set a deadline
- Edit the deadline of an existing task using the Edit button on each task item
- Mark tasks as complete by checking the checkbox, which strikes through the task name and grays it out
- Completed tasks cannot have their deadline edited
- Remove tasks from the list using the Remove button
- The total number of tasks is displayed at the top of the screen

**Components used:** EditText, Button, RecyclerView, TextView, CheckBox, DatePickerDialog, TimePickerDialog

## Time Spent
Approximately 3-4 hours.

## Most Challenging Parts
The most challenging aspect of this project was an iterative design process —
requirements evolved as the app took shape:

- Understanding the project structure of Basic Views Activity and identifying
  which layout files to modify
- Realizing the global Time Picker was disconnected from individual tasks,
  and redesigning the flow so a date and time picker automatically appears
  when adding a new task
- Discovering that a time-only picker was insufficient for meaningful deadlines,
  and adding a DatePickerDialog to capture a full timestamp
- Adding a minimum date constraint to prevent users from selecting past dates
- Improving the strike through interaction from a tap gesture to a Checkbox
  for clarity and intuitiveness
- Disabling the Edit button for completed tasks to maintain logical consistency

## Resources Used
- Android Studio Documentation: https://developer.android.com/studio
- Assignment reference video: https://uw.neetorecord.com/watch/f5e740af27e1c9becd28