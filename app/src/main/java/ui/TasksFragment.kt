package ui

import android.content.Intent
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pomotodo.R
import com.example.pomotodo.Task
import com.example.pomotodo.TasksViewModel
import com.example.pomotodo.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*


private const val ID = "id"
private const val IS_FINISHED = "isFinished"
class TasksFragment : Fragment() {
    private lateinit var fab:FloatingActionButton
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var recyclerView: RecyclerView
    private var tasksAdapter = TaskAdapter(emptyList())
    private lateinit var fragmentView:View
    private lateinit var tasks: List<Task>
    private lateinit var toolbar: Toolbar
    private lateinit var notTasksTextView: TextView

    private lateinit var fromTask: Task
    private lateinit var toTask: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = View.inflate(context,
            R.layout.fragment_tasks,null)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab = view.findViewById(R.id.fab)
        recyclerView = view.findViewById(R.id.recycler_view)
        notTasksTextView = view.findViewById(R.id.no_tasks_text_view)
        toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        //Customizing toolbar
        customizeToolbar(toolbar)
        tasksViewModel.tasksLiveData.observe(viewLifecycleOwner,
            Observer {
                this.tasks = it
                if (tasks.isEmpty()){
                    notTasksTextView.visibility = View.VISIBLE
                }
                else{
                    notTasksTextView.visibility = View.GONE
                    updateTasks(tasks)
                }


            })

        recyclerView.layoutManager = LinearLayoutManager(context)



        fab.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_taskActivity)
        }

        //Swiping
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    Collections.swap(tasks , fromPosition , toPosition)
                   recyclerView.adapter?.notifyItemMoved(fromPosition , toPosition)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when(direction) {
                        ItemTouchHelper.RIGHT -> {
                            Utils.currentPmodoro = tasks[viewHolder.adapterPosition].title
                            Utils.currentId = tasks[viewHolder.adapterPosition].id
                            Utils.currentDescription = tasks[viewHolder.adapterPosition].description
                            Utils.currentPomodoros = tasks[viewHolder.adapterPosition].pomodoros
                            findNavController().navigate(R.id.action_tasksFragment_to_pomodoroFragment2)
                        }
                        ItemTouchHelper.LEFT -> deleteWhenSwipe(viewHolder)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        //Swiping left
                        .addSwipeLeftBackgroundColor( ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        ))
                        .addSwipeRightActionIcon(R.drawable.ic_clock)
                        //Swiping right
                        .addSwipeRightBackgroundColor( ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        ))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate()

                }

            })

        itemTouchHelper.attachToRecyclerView(recyclerView)


    }



    private fun updateTasks (tasks:List<Task>) {
        tasksAdapter = TaskAdapter((tasks))
        recyclerView.adapter = tasksAdapter
    }

   inner class TaskAdapter (private val tasks:List<Task> )  : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        inner class TaskViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener {
            private val titleTextView : TextView = itemView.findViewById(R.id.title_text_view)
            private val descriptionTextView : TextView = itemView.findViewById(R.id.description_text_view)
            private val stateImageView : ImageView = itemView.findViewById(R.id.state)
            private val pomoCountTextView : TextView = itemView.findViewById(R.id.pomo_count)

            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            fun bind(task: Task) {
                titleTextView.text = task.title
                descriptionTextView.text = task.description
                pomoCountTextView.text = task.pomodoros.toString()
                if (task.isFinished == 1) {
                    stateImageView.visibility = View.VISIBLE
                }
                if (task.pomodoros != 0) {
                    pomoCountTextView.visibility = View.VISIBLE
                }

            }

            override fun onClick(v: View?) {
                val intent = Intent(requireContext() , UpdateTaskActivity::class.java)
                intent.putExtra(ID,tasks[adapterPosition].id)
                intent.putExtra(IS_FINISHED,tasks[adapterPosition].isFinished)
                startActivity(intent)
            }

            override fun onLongClick(v: View): Boolean {

                return true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val cardView : CardView =
                LayoutInflater.from(parent.context).inflate(R.layout.task_item,parent,false) as CardView

            return TaskViewHolder(cardView)
        }

        override fun getItemCount(): Int {
            return tasks.size
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }
    }

    private fun deleteWhenSwipe(holder: RecyclerView.ViewHolder){
        val currentTask = tasks[holder.adapterPosition]
//        tasksAdapter.notifyItemRemoved(holder.adapterPosition)
        tasksViewModel.deleteTask(tasks[holder.adapterPosition])

        val snackBar : Snackbar = Snackbar.make(fragmentView.findViewById(R.id.coordinator),"${currentTask.title} deleted.",
            Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo"
        ) {
            tasksViewModel.insertTask(currentTask)
        }
        snackBar.show()
    }

    private fun swipeRows (task1 : Task , task2: Task) {

    }

    private fun customizeToolbar(toolbar: Toolbar){
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar.overflowIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        toolbar.title = ""
        toolbar.subtitle = ""
    }
}