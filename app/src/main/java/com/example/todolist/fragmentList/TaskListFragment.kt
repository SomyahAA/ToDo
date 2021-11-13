package com.example.todolist.fragmentList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.dataBase.Task
import com.example.todolist.taskFragement.TaskFragment
import com.example.todolist.taskFragement.dateFormat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import android.view.View
import android.widget.CheckBox


const val key_Id = "myTaskId"


class TaskListFragment(context: Context) : Fragment() {


    private lateinit var taskRecyclerView: RecyclerView
    private val taskListViewModel by lazy { ViewModelProvider(this).get(com.example.todolist.fragmentList.TaskListViewModel::class.java) }
    private lateinit var fab: FloatingActionButton
    private lateinit var isDoneCheckBox1 :CheckBox





    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)


        taskRecyclerView = view.findViewById(R.id.task_Recycler_View)
        fab = view.findViewById(R.id.add_fab)
        //isDoneCheckBox1= view.findViewById(R.id.isDoneCheckBox)

        val linearlayoutManger = LinearLayoutManager(context)
        taskRecyclerView.layoutManager = linearlayoutManger

        return view
    }

    var tasks = listOf<Task>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskListViewModel.taskIdLiveData.observe(
            viewLifecycleOwner, Observer {
                updateUI(it)
                tasks = it
            })

        val swipeToDeleteObj = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = tasks[viewHolder.adapterPosition]
                taskListViewModel.deleteTask(task)
            }

            override fun onChildDrawOver(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                super.onChildDrawOver(
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
                    .addSwipeRightBackgroundColor(Color.RED)
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate()
            }
        }
        val swipeToDelete = ItemTouchHelper(swipeToDeleteObj)
        swipeToDelete.attachToRecyclerView(taskRecyclerView)
    }

    private fun updateUI(tasks: List<Task>) {

        val taskAdapter = TaskAdapter(tasks)
        taskRecyclerView.adapter = taskAdapter
    }

    private inner class TaskHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var task: Task

        val titleTextView: TextView = itemView.findViewById(R.id.taskTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.taskDateItem)
        val dueDateTextView: TextView = itemView.findViewById(R.id.dueDate)
        val priorityText: TextView = itemView.findViewById(R.id.prioritytv)
        val isDoneTextView: CheckBox = itemView.findViewById(R.id.isDoneCheckBox)

        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(task: Task) {

            this.task = task

            titleTextView.text = task.title
            isDoneTextView.isChecked=task.isDone


            if (task.date != null) {
                dateTextView.text = android.text.format.DateFormat.format(dateFormat, task.date)
                if (!task.isDone){
                    if (task.currentDate == task.date){
                        dueDateTextView.text="Today"
                    }
                    else if (task.currentDate.after(task.date)) {
                        dueDateTextView.text = "dueDate is passed"
                    }
                }
            }

            when (task.TaskPriority) {
                0 -> {
                    priorityText.text = "high"
                    priorityText.setTextColor(Color.RED)
                }
                1 -> {
                    priorityText.text = "medium"
                    priorityText.setTextColor(Color.GREEN)
                }
                2 -> {
                    priorityText.text = "low"
                    priorityText.setTextColor(Color.BLUE)
                    priorityText.setHintTextColor(Color.BLUE)
                }
            }

            dateTextView.visibility = if (task.date != null) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            when (v) {
                itemView -> {

                    val args = Bundle()
                    args.putSerializable(key_Id, task.id)
                    val fragment = TaskFragment()
                    fragment.arguments = args
                    activity?.let {
                        it.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_Container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
    }

    private inner class TaskAdapter(var tasks: List<Task>) : RecyclerView.Adapter<TaskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val view = layoutInflater.inflate(R.layout.listitemtask, parent, false)
            return TaskHolder(view)
        }

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }

        override fun getItemCount(): Int = tasks.size
    }

    override fun onStart() {
        super.onStart()

        fab.setOnClickListener {

            val task = Task()
            taskListViewModel.addTask(task)
            val args = Bundle()
            args.putSerializable(key_Id, task.id)
            val fragment = TaskFragment()
            fragment.arguments = args

            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_Container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
//            if (task != null){
//                task.isDone.let {
//                    isDoneCheckBox1.setOnCheckedChangeListener { _, isChecked ->
//                        task.isDone = isChecked
//                    }
//                }
//            }

        }

    }
}