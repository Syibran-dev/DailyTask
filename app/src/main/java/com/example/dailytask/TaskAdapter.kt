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
    private val isEditable: Boolean = true // Parameter baru, default true
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvTaskDate: TextView? = itemView.findViewById(R.id.tvTaskDate) // Optional jika ada di layout
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.tvTaskName.text = task.taskName
        holder.cbTask.isChecked = task.isDone
        // Disable checkbox if not editable
        holder.cbTask.isEnabled = isEditable
        
        // Tampilkan tanggal jika viewnya ada (untuk future proofing layout item_task)
        if (task.taskDate.isNotEmpty()) {
             holder.tvTaskName.text = "${task.taskName} (${task.taskDate})"
        }

        updateStrikeThrough(holder.tvTaskName, task.isDone)

        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }

        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            if (isEditable) { // Pastikan hanya trigger jika editable
                updateStrikeThrough(holder.tvTaskName, isChecked)
                onStatusChange(task, isChecked)
            } else {
                // Jika user mencoba klik (walaupun disabled, just safety)
                holder.cbTask.isChecked = !isChecked 
            }
        }

        holder.btnDelete.setOnClickListener {
            onDelete(task)
        }
        
        // Hide delete button if not editable (User view) -> User can delete their own tasks? 
        // Prompt says "user tambah tugas... admin menentukan selesai".
        // Usually users can delete what they created. I will keep delete enabled for now.
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
