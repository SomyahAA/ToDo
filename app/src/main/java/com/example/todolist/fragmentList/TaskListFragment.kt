package com.example.todolist.fragmentList

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.dataBase.Task
import com.example.todolist.taskFragement.TaskFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton



const val key_Id = "myTaskId"

class TaskListFragment : Fragment() {


    private lateinit var taskRecyclerView: RecyclerView
    private val taskListViewModel by lazy { ViewModelProvider(this).get(com.example.todolist.fragmentList.TaskListViewModel::class.java) }
    private lateinit var fab: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        taskRecyclerView = view.findViewById(R.id.task_Recycler_View)
        fab = view.findViewById(R.id.add_fab)

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
                tasks =it
            })

            val swipeToDeleteObj = object : ItemTouchHelper.SimpleCallback(
            0,ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val task=tasks[viewHolder.adapterPosition]
                taskListViewModel.deleteTask(task)

            }
        }
       val swipeToDelete =ItemTouchHelper(swipeToDeleteObj)
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


        private val isDoneCheckBox: CheckBox = itemView.findViewById(R.id.isDoneCheckBox)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(task: Task) {
            this.task = task
            titleTextView.text = task.title
            dateTextView.text = task.date.toString()
            isDoneCheckBox.visibility = if (task.isDone) {
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
                dateTextView -> {
                    Toast.makeText(context, "the date is ${task.date}", Toast.LENGTH_LONG)
                        .show()
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
        }
    }


}


