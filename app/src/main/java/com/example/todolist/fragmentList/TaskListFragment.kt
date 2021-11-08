package com.example.todolist.fragmentList

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.dataBase.Task
import com.example.todolist.taskFragement.TaskFragment


const val key_Id ="myTaskId"

class TaskListFragment : Fragment() {



        private  lateinit var taskRecyclerView: RecyclerView
        private val taskListViewModel by lazy {ViewModelProvider (this ).get(com.example.todolist.fragmentList.TaskListViewModel::class.java)}

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view =inflater.inflate(R.layout.fragment_task_list, container, false)

            taskRecyclerView=view.findViewById(R.id.task_Recycler_View)
            val linearlayoutManger =LinearLayoutManager(context)
            taskRecyclerView.layoutManager = linearlayoutManger

            return view

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            taskListViewModel.taskIdLiveData.observe(
                viewLifecycleOwner, Observer {
                    updateUI(it)
                })

        }



        private fun updateUI(tasks: List<Task> ) {

            val taskAdapter = TaskAdapter(tasks)
            taskRecyclerView.adapter = taskAdapter
        }

        private inner class TaskHolder(view:View) : RecyclerView.ViewHolder(view),View.OnClickListener{

            private lateinit var task: Task

            val titleTextView:TextView=itemView.findViewById(R.id.taskTitle)
            val dateTextView:TextView=itemView.findViewById(R.id.taskDateItem)

            private val isDoneCheckBox:CheckBox=itemView.findViewById(R.id.isDoneCheckBox)
            init {
                itemView.setOnClickListener(this)


            }
            fun bind(task : Task){
                this.task=task
                titleTextView.text=task.title
                dateTextView.text=task.date.toString()
                isDoneCheckBox.visibility =if (task.isDone){
                    View.VISIBLE
                }else {
                    View.GONE
                }
            }

            override fun onClick(v: View?) {
                if (v == itemView){
                    val args =Bundle()
                    args.putSerializable(key_Id,task.id)
                    val fragment = TaskFragment()
                    fragment.arguments =args

                    activity?.let {
                        it.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_Container,fragment)
/*2*/                   .addToBackStack(null) // this is for making pages above each other and don't delete the other page
                            .commit()
                    }

                }else if(v == dateTextView){
                    Toast.makeText(context,"the date is ${task.date}",Toast.LENGTH_LONG).show()
                }
            }
        }

        private inner class TaskAdapter(var tasks: List<Task>) : RecyclerView.Adapter<TaskHolder>(){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
                val view = layoutInflater.inflate(R.layout.listitemtask, parent, false)
                return TaskHolder(view)
            }

            override fun onBindViewHolder(holder: TaskHolder, position: Int) {
                val crime = tasks[position]
                holder.bind(crime)
            }

            override fun getItemCount(): Int = tasks.size

        }



    }
