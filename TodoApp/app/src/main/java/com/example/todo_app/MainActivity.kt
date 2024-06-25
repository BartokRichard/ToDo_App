package com.example.todo_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var pendingTasksRecyclerView: RecyclerView
    private lateinit var completedTasksRecyclerView: RecyclerView
    private lateinit var addTaskButton: Button
    private lateinit var tasks: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        pendingTasksRecyclerView = findViewById(R.id.pendingTasksRecyclerView)
        completedTasksRecyclerView = findViewById(R.id.completedTasksRecyclerView)
        addTaskButton = findViewById(R.id.addTaskButton)

        tasks = loadTasks()

        setupRecyclerViews()

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
