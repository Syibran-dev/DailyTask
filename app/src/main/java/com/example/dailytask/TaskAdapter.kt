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
    private val onStatusChange: (TaskModel, Boolean) -> Unit, // Callback saat checkbox diklik
    private val onDelete: (TaskModel) -> Unit // Callback saat hapus diklik
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.tvTaskName.text = task.name
        holder.cbTask.isChecked = task.isDone

        // Efek Coret jika selesai
        updateStrikeThrough(holder.tvTaskName, task.isDone)

        // Logic Checkbox
        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            updateStrikeThrough(holder.tvTaskName, isChecked)
            onStatusChange(task, isChecked)
        }

        // Logic Hapus
        holder.btnDelete.setOnClickListener {
            onDelete(task)
        }
    }

    private fun updateStrikeThrough(textView: TextView, isDone: Boolean) {
        if (isDone) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.alpha = 0.5f // Agak transparan
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.alpha = 1.0f // Jelas
        }
    }

    override fun getItemCount(): Int = taskList.size

    // Fungsi untuk refresh data
    fun updateData(newList: ArrayList<TaskModel>) {
        taskList.clear()
        taskList.addAll(newList)
        notifyDataSetChanged()
    }
}