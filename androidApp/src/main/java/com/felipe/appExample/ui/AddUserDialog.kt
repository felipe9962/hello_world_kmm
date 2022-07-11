package com.felipe.appExample.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import com.felipe.appExample.android.R
import com.felipe.appExample.android.databinding.FragmentAddUserDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddUserDialog : BottomSheetDialogFragment() {
    interface OnUserListener {
        fun onUserPopulate(name: String, time: Long)
    }


    private lateinit var binding: FragmentAddUserDialogBinding
    private lateinit var listener: OnUserListener

    private var name: String? = null
    private var time: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUserDialogBinding.inflate(inflater)

        prepareButtons()
        prepareEditText()


        return binding.root
    }

    private fun prepareEditText() {
        binding.ibSendUser.isEnabled = false
        binding.etName.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.ibSendUser.isEnabled = s.isNotEmpty()
            }
        })
    }

    fun setListener(listener: OnUserListener) {
        this.listener = listener
    }

    private fun prepareButtons() {
        binding.ibSendUser.setOnClickListener {
            if (binding.etName.text.isEmpty())
                return@setOnClickListener

            name = binding.etName.text.toString()
            selectDate()

        }
    }

    private fun selectDate() {
        val pickDateDialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select birthdate of the user")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        pickDateDialog.addOnPositiveButtonClickListener {
            time = it
            selectHour()
        }

        pickDateDialog.show(parentFragmentManager, null)
    }

    private fun selectHour() {
        val isSystem24Hour = is24HourFormat(activity)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select birthdate")
                .build()

        picker.addOnPositiveButtonClickListener {
            time += picker.hour * 60 * 60 * 1000 + picker.minute * 60 * 1000

            listener.onUserPopulate(name!!, time)
        }

        picker.show(parentFragmentManager, null)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddUserDialog()/*.apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }*/
    }
}