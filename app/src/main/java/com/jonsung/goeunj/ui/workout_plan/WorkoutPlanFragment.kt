package com.jonsung.goeunj.ui.workout_plan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.jonsung.goeunj.R
import com.jonsung.goeunj.databinding.FragmentWorkoutBinding
import com.jonsung.goeunj.utils.convertDateToString
import com.jonsung.goeunj.utils.convertStringToDate
import com.jonsung.goeunj.utils.getStringSharedPreferences
import com.jonsung.goeunj.utils.setStringSharedPreferences
import java.text.SimpleDateFormat
import java.util.*


class WorkoutPlanFragment : Fragment() {

    private lateinit var layout: FragmentWorkoutBinding
    private lateinit var workoutPlanViewModel: WorkoutPlanViewModel
    private lateinit var workoutAdapter: WorkoutPlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_workout, container, false)

        workoutPlanViewModel = ViewModelProvider(this).get(WorkoutPlanViewModel::class.java)

        workoutAdapter = WorkoutPlanAdapter()
        layout.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workoutAdapter
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                workoutAdapter.swapWorkoutOrders(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(layout.recyclerView)

        with (workoutPlanViewModel) {
            setSelectedDate(getDefaultSelectedDate())

            selectedDate.observe(viewLifecycleOwner, {
                Log.d("selectedDate", it.convertDateToString())
                val date = it.convertDateToString()

                layout.selectedDate.text = date
                displayWorkoutsForSelectedDate(date)

                activity?.setStringSharedPreferences(R.string.preference_selected_date_key, date)
            })
        }

        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (layout) {
            title.text = getString(R.string.workout_fragment_header)
            showCalendarButton.text = getString(R.string.show_workout_calendar_button_text)

            showCalendarButton.setOnClickListener {
                displayCalendar()
            }
        }
    }

    private fun getDefaultSelectedDate(): Date {
        val defaultDate = activity?.getStringSharedPreferences(R.string.preference_selected_date_key)
        return when {
            defaultDate.isNullOrEmpty() -> Date()
            else -> defaultDate.convertStringToDate() ?: Date()
        }
    }

    private fun displayCalendar() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(getDefaultSelectedDate().time)
            .build()

        datePicker.show(requireActivity().supportFragmentManager, "fragment_workout_plan")

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Workaround: Bug with Material DatePicker: https://github.com/material-components/material-components-android/issues/882
            val utcTime = Date(selection)
            val format = "yyy/MM/dd HH:mm:ss"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val defaultTime = SimpleDateFormat(format, Locale.getDefault()).parse(sdf.format(utcTime))

            defaultTime?.let { date ->
                workoutPlanViewModel.setSelectedDate(Date(date.time))
            }
        }
    }

    private fun displayWorkoutsForSelectedDate(date: String) {
        workoutPlanViewModel.weeklyWorkoutPlans[date]?.let { workouts ->
            workoutAdapter.setWorkouts(workouts)
        }
    }
}