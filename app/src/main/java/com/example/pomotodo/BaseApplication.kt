package com.example.pomotodo

import android.app.Application

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TasksRepository.initialize(this)
    }


}