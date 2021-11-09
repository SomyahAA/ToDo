package com.example.todolist.dataBase

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors


private const val DATABASE_NAME = "task-database"

class TaskRepository private constructor(context: Context) {

    private val executor = Executors.newSingleThreadExecutor()

    private val database: TaskDatabase = Room.databaseBuilder(
        context.applicationContext,
        TaskDatabase::class.java,
        DATABASE_NAME
    ).build()


    private val taskDao = database.taskDao()

    fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    fun getTask(id: UUID): LiveData<Task?> {
        return taskDao.getTask(id)
    }

    fun updateTask(task: Task) {
        executor.execute {
            taskDao.updateTask(task)
        }
    }

    fun addTask(task: Task) {
        executor.execute {
            taskDao.addTask(task)
        }
    }

    fun deleteTask(task: Task) {
        executor.execute {
            taskDao.deleteTask(task)
        }
    }

    companion object {
        private var INSTANCE: TaskRepository? =
            null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TaskRepository(context)
            }
        }

        fun get(): TaskRepository {
            return INSTANCE ?: throw IllegalStateException("TaskRepository must be initialized")
        }
    }
}
