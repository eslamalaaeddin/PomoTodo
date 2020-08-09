package com.example.pomotodo

import androidx.lifecycle.ViewModel

private const val TAG = "TasksViewModel"
class TasksViewModel : ViewModel() {

    private val tasksRepository = TasksRepository.get()

    //Tasks liveData
    val tasksLiveData = tasksRepository.getTasks()

    fun getTask(id:Int) = tasksRepository.getTask(id)

    //Insert task
    fun insertTask (task: Task) = tasksRepository.insertTask(task)

    //Delete task
    fun deleteTask (task: Task) = tasksRepository.deleteTask(task)

    //Update task
    fun updateTask (task: Task) = tasksRepository.updateTask(task)

}