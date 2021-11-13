package com.example.todolist.taskFragement


import TaskFragmentViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.R
import com.example.todolist.dataBase.Task
import com.example.todolist.fragmentList.TaskListFragment
import com.example.todolist.fragmentList.key_Id
import java.lang.System.load
import java.util.*


const val task_date_key = "taskDate"
const val dateFormat = "dd/MMM/yyyy"

class TaskFragment : Fragment(), DatePikerDialogFragment.DatePickerCallback,
    AdapterView.OnItemSelectedListener {

    private lateinit var task: Task
    private lateinit var titleEditText: EditText
    private lateinit var dateBtn: Button
    private lateinit var done: Button
    private lateinit var taskDetailsEditText: EditText
    private lateinit var spinner: Spinner
    private var position1: Int = 0


    private val fragmentViewModel by lazy { ViewModelProvider(this)[TaskFragmentViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.task_fragment, container, false)
        titleEditText = view.findViewById(R.id.taskTitle)
        dateBtn = view.findViewById(R.id.task_date)
        taskDetailsEditText = view.findViewById(R.id.taskDetailsEditText)

        done = view.findViewById(R.id.Done)

        done.setOnClickListener {

            task.title = titleEditText.text.toString()
            task.details = taskDetailsEditText.text.toString()
            task.TaskPriority = position1

            fragmentViewModel.saveUpdate(task)
            val args = Bundle()
            val fragment = context?.let { it1 -> TaskListFragment(it1) }
            if (fragment != null) {
                fragment.arguments = args
            }
            activity?.let {
                if (fragment != null) {
                    it.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_Container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        spinner = view.findViewById(R.id.priority_spinner)

        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.project_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()

        spinner.onItemSelectedListener = this

        dateBtn.setOnClickListener {
            val args = Bundle()
            args.putSerializable(task_date_key, task.date)

            val datePicker = DatePikerDialogFragment().also {
                it.setTargetFragment(this, 0)
                it.show(this.parentFragmentManager, "date Picker")
            }
            datePicker.arguments = args
        }

        val textWatcher = object :
            TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                Log.d("Somyah", sequence.toString())
                task.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        titleEditText.addTextChangedListener(textWatcher)


        val textWatcher2 = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                Log.d("Somyah", sequence.toString())
                task.details = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        taskDetailsEditText.addTextChangedListener(textWatcher2)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = Task()
        val taskId = arguments?.getSerializable(key_Id) as UUID
        fragmentViewModel.loadTask(taskId)
        Log.d("TASK FRAGMENT", taskId.toString())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewModel.taskLiveData.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    task = it
                    titleEditText.setText(it.title)
                    taskDetailsEditText.setText(it.details)
                    task.TaskPriority=position1
                    if (it.date != null) {
                        dateBtn.text = android.text.format.DateFormat.format(dateFormat, it.date)
                    }
                    taskDetailsEditText.setText(it.details)
                }
            }
        )
    }
    override fun onDateSelected(date: Date) {
        task.date = date
        dateBtn.text = android.text.format.DateFormat.format(dateFormat, task.date)
    }
    override fun onStop() {
        super.onStop()
        if (task.title.isEmpty()) {
            fragmentViewModel.deleteTask(task)
            Toast.makeText(context, "task should have a task name", Toast.LENGTH_LONG).show()
        } else {
            fragmentViewModel.saveUpdate(task)
        }
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        position1 = position
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}

