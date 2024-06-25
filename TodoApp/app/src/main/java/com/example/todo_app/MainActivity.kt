package com.example.todo_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var pendingTasksRecyclerView: RecyclerView
    private lateinit var completedTasksRecyclerView: RecyclerView
    private lateinit var addTaskButton: Button
    private lateinit var dateText: TextView
    private lateinit var tasks: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        pendingTasksRecyclerView = findViewById(R.id.pendingTasksRecyclerView)
        completedTasksRecyclerView = findViewById(R.id.completedTasksRecyclerView)
        addTaskButton = findViewById(R.id.addTaskButton)
        dateText = findViewById(R.id.dateText)

        // Set the current date
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        dateText.text = currentDate

        // Load tasks
        tasks = loadTasks()

        // Setup RecyclerViews
        setupRecyclerViews()

        // Set onClick listener for the add task button
        addTaskButton.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        tasks = loadTasks()
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        val pendingTasks = tasks.filter { !it.isCompleted }.toMutableList()
        val completedTasks = tasks.filter { it.isCompleted }.toMutableList()

        pendingTasksRecyclerView.layoutManager = LinearLayoutManager(this)
        pendingTasksRecyclerView.adapter = TaskAdapter(pendingTasks, { task ->
            onTaskStatusChanged(task)
        }, { task ->
            onTaskLongClicked(task)
        })

        completedTasksRecyclerView.layoutManager = LinearLayoutManager(this)
        completedTasksRecyclerView.adapter = TaskAdapter(completedTasks, { task ->
            onTaskStatusChanged(task)
        }, { task ->
            onTaskLongClicked(task)
        })
    }

    private fun onTaskStatusChanged(task: Task) {
        task.isCompleted = !task.isCompleted
        saveTasks(tasks)
        setupRecyclerViews()
    }

    private fun onTaskLongClicked(task: Task) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Task")
        builder.setMessage("Are you sure you want to delete this task?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            tasks.remove(task)
            saveTasks(tasks)
            setupRecyclerViews()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
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
