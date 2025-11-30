package com.example.dailytask

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: ArrayList<TaskModel>,
    private val onTaskClick: (TaskModel) -> Unit,
    private val onStatusChange: (TaskModel, Boolean) -> Unit,
    private val onDelete: (TaskModel) -> Unit,
    private val isEditable: Boolean = true 
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvTaskDate: TextView? = itemView.findViewById(R.id.tvTaskDate) 
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // SHOW OWNER NAME IF AVAILABLE (For Global Admin View)
        if (task.ownerName.isNotEmpty()) {
            holder.tvTaskName.text = "${task.taskName} (${task.ownerName})"
        } else {
            holder.tvTaskName.text = task.taskName
        }

        // Clear listener before setting state to avoid triggering callback
        holder.cbTask.setOnCheckedChangeListener(null)
        holder.cbTask.isChecked = task.isDone
        holder.cbTask.isEnabled = isEditable
        
        if (holder.tvTaskDate != null) {
            if (task.taskDate.isNotEmpty()) {
                holder.tvTaskDate.visibility = View.VISIBLE
                holder.tvTaskDate.text = task.taskDate
            } else {
                holder.tvTaskDate.visibility = View.GONE
            }
        }

        updateStrikeThrough(holder.tvTaskName, task.isDone)

        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }

        // Set listener back
        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            if (isEditable) { 
                updateStrikeThrough(holder.tvTaskName, isChecked)
                onStatusChange(task, isChecked)
            } else {
                holder.cbTask.isChecked = !isChecked 
            }
        }

        holder.btnDelete.setOnClickListener {
            onDelete(task)
        }
    }

    private fun updateStrikeThrough(textView: TextView, isDone: Boolean) {
        if (isDone) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.alpha = 0.5f
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.alpha = 1.0f
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun updateData(newList: ArrayList<TaskModel>) {
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }
}
