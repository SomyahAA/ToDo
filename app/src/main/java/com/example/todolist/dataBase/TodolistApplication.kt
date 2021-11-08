package com.example.todolist.dataBase

import android.app.Application

class TodolistApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
    }
}