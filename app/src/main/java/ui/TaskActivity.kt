package ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.pomotodo.R
import com.example.pomotodo.Task
import com.example.pomotodo.TasksViewModel

class TaskActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var tasksViewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel::class.java)

        titleEditText = findViewById(R.id.title_edit_text)
        descriptionEditText = findViewById(R.id.description_edit_text)
        saveButton = findViewById(R.id.save_update_button)

        saveButton.setOnClickListener {
            addTask()
        }

    }

    private fun addTask() {
        val title = titleEditText.editableText.toString()
        val description = descriptionEditText.editableText.toString()

        if (title.isEmpty() || description.isEmpty() ) {
            Toast.makeText(this, "Invalid title or description!", Toast.LENGTH_SHORT).show()
        }
        else{
            val task = Task(title, description)
            tasksViewModel.insertTask(task)
            Toast.makeText(this, "${task.title} added.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}