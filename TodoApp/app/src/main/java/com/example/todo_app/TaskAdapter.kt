package com.example.todo_app

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskStatusChanged: (Task) -> Unit,
    private val onTaskLongClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskIcon: ImageView = itemView.findViewById(R.id.taskIcon)
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskTime: TextView = itemView.findViewById(R.id.taskTime)
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.taskTitle.text = task.title
        holder.taskTime.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(task.time ?: 0L)
        holder.taskCheckBox.isChecked = task.isCompleted
        holder.taskIcon.setImageResource(task.categoryIcon)

        if (task.isCompleted) {
            holder.taskTitle.paintFlags = holder.taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.taskTime.paintFlags = holder.taskTime.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.taskTitle.paintFlags = holder.taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.taskTime.paintFlags = holder.taskTime.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.taskCheckBox.setOnCheckedChangeListener { _, _ ->
            onTaskStatusChanged(task)
        }

        holder.itemView.setOnLongClickListener {
            onTaskLongClicked(task)
            true
        }
    }

    override fun getItemCount(): Int = tasks.size
}
