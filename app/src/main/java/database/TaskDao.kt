package database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pomotodo.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getTasks () : LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id=(:id)")
    fun getTask(id:Int) : LiveData<Task>

    @Insert
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}