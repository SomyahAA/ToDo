package com.example.todolist.taskFragement


import TaskFragmentViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.R
import com.example.todolist.dataBase.Task
import com.example.todolist.fragmentList.key_Id
import java.util.*


const val task_date_key = "taskDate"

class TaskFragment : Fragment(), DatePikerDialogFragment.DatePickerCallback {

    private lateinit var task: Task
    private lateinit var titleEditText: EditText
    private lateinit var dateBtn: Button
    private lateinit var button_save:Button

    private val fragmentViewModel by lazy { ViewModelProvider(this).get(TaskFragmentViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.task_fragment, container, false)
        titleEditText = view.findViewById(R.id.taskTitle)
        dateBtn = view.findViewById(R.id.task_date)
        button_save =view.findViewById(R.id.button_save)


        dateBtn.apply {
            text = task.date.toString()

        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dateBtn.setOnClickListener {

            val args = Bundle()
            args.putSerializable(task_date_key, task.date)
            val datePicker = DatePikerDialogFragment().also {
                it.setTargetFragment(this, 0)
                it.show(this.parentFragmentManager, "date Picker")
            }

            datePicker.arguments = args
        }
        button_save.setOnClickListener{

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
                Log.d("Sam", sequence.toString())
                task.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        titleEditText.addTextChangedListener(textWatcher)
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
                    dateBtn.text = it.date.toString()
                }
            }
        )
    }

    override fun onDateSelected(date: Date) {
        task.date = date
        dateBtn.text = date.toString()
    }

    override fun onStop() {
        super.onStop()
        fragmentViewModel.saveUpdate(task)
    }

}

