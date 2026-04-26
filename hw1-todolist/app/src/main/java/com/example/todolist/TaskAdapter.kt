package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.app.DatePickerDialog
import android.app.TimePickerDialog

// Strike Through
import android.graphics.Paint

// Checkbox
import android.widget.CheckBox

class TaskAdapter(
    private val tasks: MutableList<Task>, // 存储任务列表
    private val onRemoveClick: (Int) -> Unit // 点击 Remove 按钮时要做什么 （回调函数）
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        val btnPickTime: Button = itemView.findViewById(R.id.btnPickTime)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemove)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTaskName.text = task.name
        holder.btnPickTime.isEnabled = !task.isDone

        // strike through
        if (task.isDone) {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Checkbox
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = task.isDone

        if (task.isDone) {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTaskName.setTextColor(android.graphics.Color.GRAY)
        } else {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTaskName.setTextColor(android.graphics.Color.BLACK)
        }

        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            tasks[position] = task.copy(isDone = isChecked)
            notifyItemChanged(position)
        }

        if (task.isDone) {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTaskName.setTextColor(android.graphics.Color.GRAY)
        } else {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTaskName.setTextColor(android.graphics.Color.BLACK)
        }

        holder.btnPickTime.isEnabled = !task.isDone // 新增这一行

        if (task.deadline != null) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            holder.tvDeadline.text = "Deadline: ${sdf.format(Date(task.deadline))}"
            holder.tvDeadline.visibility = View.VISIBLE
        } else {
            holder.tvDeadline.text = "No deadline set"
            holder.tvDeadline.visibility = View.VISIBLE
        }

        holder.btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            if (task.deadline != null) {
                calendar.timeInMillis = task.deadline
            }

            DatePickerDialog(holder.itemView.context, { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(holder.itemView.context, { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    tasks[position] = task.copy(deadline = calendar.timeInMillis)
                    notifyItemChanged(position)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        holder.btnRemove.setOnClickListener {
            onRemoveClick(position)
        }
    }

    override fun getItemCount(): Int = tasks.size
}