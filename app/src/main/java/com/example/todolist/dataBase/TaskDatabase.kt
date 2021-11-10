package com.example.todolist.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters



@Database(entities = [Task::class],version = 2)

@TypeConverters(TaskTypeConverter::class)


abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

}