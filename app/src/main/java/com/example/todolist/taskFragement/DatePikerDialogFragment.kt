package com.example.todolist.taskFragement

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePikerDialogFragment : DialogFragment() {

    interface DatePickerCallback {
        fun onDateSelected(date: Date)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val date = arguments?.getSerializable(task_date_key) as Date?
        val calender = Calendar.getInstance()

        if (date != null) {
            calender.time = date
        }

        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)


        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val resultData = GregorianCalendar(year, month, dayOfMonth).time
            targetFragment?.let {
                (it as DatePickerCallback).onDateSelected(resultData)
            }
        }
        return DatePickerDialog(
            requireContext(),
            dateListener,
            year,
            month,
            day
        )

    }

}