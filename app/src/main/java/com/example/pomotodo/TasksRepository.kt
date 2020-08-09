package com.example.pomotodo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import database.TasksDatabase

import database.migration_2_3
import java.util.concurrent.Executors

private const val DATABASE_NAME = "tasks-database"

class TasksRepository private constructor(context: Context) {

    //3-Creating a concrete implementation of the abstract database class
    private val database : TasksDatabase = Room.databaseBuilder(
        context.applicationContext,
        TasksDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_2_3)
    .build()


    private val executor = Executors.newSingleThreadExecutor()

    private val taskDao = database.getTaskDao()

    //4-Functions to get database queries

    //Query tasks
    fun getTasks(): LiveData<List<Task>> = taskDao.getTasks()

    //Query task
    fun getTask(id: Int): LiveData<Task> = taskDao.getTask(id)

    //Insert task
    fun insertTask (task: Task) {
        executor.execute {
            taskDao.insertTask(task)
        }
    }

    //Delete task
    fun deleteTask (task: Task){
        executor.execute {
            taskDao.deleteTask(task)
        }
    }

    //Update task
    fun updateTask (task: Task){
        executor.execute {
            taskDao.updateTask(task)
        }
    }


    //1-Creating an instance of the Repo.
    companion object {
        private var INSTANCE: TasksRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TasksRepository(context)
            }
        }

        //2-Accessing the Repo.
        fun get(): TasksRepository {
            return INSTANCE ?: throw IllegalStateException("TasksRepository must be initialized")
        }

    }
}