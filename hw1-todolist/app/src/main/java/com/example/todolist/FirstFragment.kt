package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar

// 对应 fragment_first.xml，我们主要在这里写逻辑


class FirstFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter

    //taskList： 存所有任务的列表，是我们的数据来源
    private val taskList = mutableListOf<Task>()

    // onCreateView：把 fragment_first.xml 加载成实际的视图
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }
    // 视图创建完后，在这里找到各个控件并设置逻辑
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTaskInput = view.findViewById<EditText>(R.id.etTaskInput)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        val rvTasks = view.findViewById<RecyclerView>(R.id.rvTasks)
        val tvItemCount = view.findViewById<TextView>(R.id.tvItemCount)

        taskAdapter = TaskAdapter(taskList) { position ->
            taskList.removeAt(position)
            taskAdapter.notifyItemRemoved(position)
            taskAdapter.notifyItemRangeChanged(position, taskList.size)
            tvItemCount.text = "Items: ${taskList.size}"
        }

        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = taskAdapter


        btnAdd.setOnClickListener {
            val taskName = etTaskInput.text.toString().trim()
            if (taskName.isNotEmpty()) {
                showDateTimePicker { timestamp ->
                    taskList.add(Task(taskName, timestamp))
                    taskAdapter.notifyItemInserted(taskList.size - 1)
                    tvItemCount.text = "Items: ${taskList.size}"
                    etTaskInput.text.clear()
                }
            }
        }
    }
    private fun showDateTimePicker(onDateTimeSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                onDateTimeSelected(calendar.timeInMillis)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()

    }
}