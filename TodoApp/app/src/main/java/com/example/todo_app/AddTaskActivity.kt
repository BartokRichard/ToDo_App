package com.example.todo_app

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskTitle: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var saveButton: Button

    private lateinit var eventIcon: ImageView
    private lateinit var exerciseIcon: ImageView
    private lateinit var studyIcon: ImageView
    private lateinit var taskIcon: ImageView

    private var selectedCategory: String = "Task"
    private var selectedCategoryIcon: Int = R.drawable.ic_task // Alapértelmezett ikon
    private var selectedDate: Long? = null
    private var selectedTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskTitle = findViewById(R.id.taskTitle)
        dateButton = findViewById(R.id.dateButton)
        timeButton = findViewById(R.id.timeButton)
        saveButton = findViewById(R.id.saveButton)

        eventIcon = findViewById(R.id.eventIcon)
        exerciseIcon = findViewById(R.id.exerciseIcon)
        studyIcon = findViewById(R.id.studyIcon)
        taskIcon = findViewById(R.id.taskIcon)

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        timeButton.setOnClickListener {
            showTimePickerDialog()
        }

        saveButton.setOnClickListener {
            saveTask()
        }

        eventIcon.setOnClickListener {
            selectedCategory = "Event"
            selectedCategoryIcon = R.drawable.ic_event
            highlightSelectedCategory()
        }

        exerciseIcon.setOnClickListener {
            selectedCategory = "Exercise"
            selectedCategoryIcon = R.drawable.ic_exercise
            highlightSelectedCategory()
        }

        studyIcon.setOnClickListener {
            selectedCategory = "Study"
            selectedCategoryIcon = R.drawable.ic_study
            highlightSelectedCategory()
        }

        taskIcon.setOnClickListener {
            selectedCategory = "Task"
            selectedCategoryIcon = R.drawable.ic_task
            highlightSelectedCategory()
        }
    }

    private fun highlightSelectedCategory() {
        eventIcon.background = if (selectedCategory == "Event") getDrawable(R.drawable.selected_icon_background) else null
        exerciseIcon.background = if (selectedCategory == "Exercise") getDrawable(R.drawable.selected_icon_background) else null
        studyIcon.background = if (selectedCategory == "Study") getDrawable(R.drawable.selected_icon_background) else null
        taskIcon.background = if (selectedCategory == "Task") getDrawable(R.drawable.selected_icon_background) else null
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
                dateButton.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime = calendar.timeInMillis
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                timeButton.text = formattedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun saveTask() {
        val title = taskTitle.text.toString()

        if (title.isBlank()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTime == null) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(title, selectedDate, selectedTime, selectedCategory, false, selectedCategoryIcon)
        val tasks = loadTasks()
        tasks.add(task)
        saveTasks(tasks)

        scheduleNotification(task)

        finish()
    }

    private fun scheduleNotification(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", task.title)
            putExtra("notificationId", task.hashCode()) // U ID to each task
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            task.hashCode(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTime = (task.time ?: 0L) - 10 * 60 * 1000 // 10 min earlier
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
    }

    private fun loadTasks(): MutableList<Task> {
        val file = File(filesDir, "tasks.json")
        if (!file.exists()) return mutableListOf()

        val type = object : TypeToken<MutableList<Task>>() {}.type
        return Gson().fromJson(file.readText(), type)
    }

    private fun saveTasks(tasks: MutableList<Task>) {
        val file = File(filesDir, "tasks.json")
        file.writeText(Gson().toJson(tasks))
    }
}
