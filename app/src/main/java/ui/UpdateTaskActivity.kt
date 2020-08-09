package ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.pomotodo.R
import com.example.pomotodo.Task
import com.example.pomotodo.TasksViewModel

private const val ID = "id"
private const val IS_FINISHED = "isFinished"
private const val TAG = "UpdateTaskActivity"
class UpdateTaskActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var tasksViewModel: TasksViewModel
    var myId = 0
    var isFinished = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)



        tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel::class.java)

        titleEditText = findViewById(R.id.title_edit_text)
        descriptionEditText = findViewById(R.id.description_edit_text)
        saveButton = findViewById(R.id.save_update_button)

         myId = intent.getIntExtra(ID,0)
         isFinished = intent.getIntExtra(IS_FINISHED,0)

        tasksViewModel.getTask(myId).observe(this, Observer {
            titleEditText.setText(it.title)
            descriptionEditText.setText(it.description)
        })

        saveButton.setOnClickListener {
            updateTask()
        }

    }

    private fun updateTask() {
        val title = titleEditText.editableText.toString()
        val description = descriptionEditText.editableText.toString()

        if (title.isEmpty() || description.isEmpty() ) {
            Toast.makeText(this, "Invalid title or description!", Toast.LENGTH_SHORT).show()
        }
        else{
            val newTask  = Task(title, description,isFinished, myId)
            tasksViewModel.updateTask(newTask)
            Toast.makeText(this, "${newTask.title} updated.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}