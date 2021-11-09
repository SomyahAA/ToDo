package com.example.todolist.fragmentList

import androidx.lifecycle.ViewModel
import com.example.todolist.dataBase.Task
import com.example.todolist.dataBase.TaskRepository


class TaskListViewModel : ViewModel() {

    private val taskRepository = TaskRepository.get()
    val taskIdLiveData = taskRepository.getAllTasks()


    fun addTask(task: Task) {
        taskRepository.addTask(task)
    }

    fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }

}