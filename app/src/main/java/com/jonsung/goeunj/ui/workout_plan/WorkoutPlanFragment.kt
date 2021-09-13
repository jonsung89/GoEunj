package com.jonsung.goeunj.ui.workout_plan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.jonsung.goeunj.MainViewModel
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.map
import com.jonsung.goeunj.data.mapFailure
import com.jonsung.goeunj.databinding.FragmentWorkoutBinding
import com.jonsung.goeunj.ui.home.HomeFragment
import com.jonsung.goeunj.utils.*
import java.text.SimpleDateFormat
import java.util.*



class WorkoutPlanFragment : Fragment() {

    companion object {
        private const val TAG = "fragment_workout_plan"
    }

    private lateinit var layout: FragmentWorkoutBinding
    private lateinit var workoutPlanViewModel: WorkoutPlanViewModel
    private lateinit var workoutAdapter: WorkoutPlanAdapter
//    private val mainViewModel: MainViewModel by activityViewModels()

    private var argSelectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            argSelectedDate = it.getString("selectedWorkoutDate")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_workout, container, false)

        workoutPlanViewModel = ViewModelProvider(this).get(WorkoutPlanViewModel::class.java)

        workoutAdapter = WorkoutPlanAdapter(requireContext()) {
            onWorkoutClicked()
        }
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

            // TODO: hardcoded eunji's userId - change back to `FirebaseUserManager.getCurrentUser()?.uid`
            val userId = "Ic7ycswLEeWRXuzgmpsVsoemrcI3"
            fetchWorkoutPlansByUserId(userId)

            workoutPlanFetchEvent.observe(viewLifecycleOwner, { loadingState ->
                loadingState
                    .map { plan ->
                        plan.goals?.let { goals ->
                            goals.firstOrNull { it.startWeek?.isDateInCurrentWeek() == true }?.let {
                                workoutPlanViewModel.setWorkoutPlansByWeek(it)
                            } ?: run {
                                Log.e(TAG, "workoutPlan not found")
                            }
                        }
                    }
                    .mapFailure { message, title -> Log.e("workoutPlanFetchEvent", message) }
            })

            workoutPlansByWeekEvent.observe(viewLifecycleOwner, {
                it[selectedDate.value]?.muscleGroup?.let { muscleGroup ->
                    workoutPlanViewModel.fetchWorkoutsByGroupByUserId(userId, muscleGroup)
                }
            })

            workoutsByGroupEvent.observe(viewLifecycleOwner, { loadingState ->
                loadingState
                    .map {
                        workoutAdapter.setWorkoutList(it.workouts ?: listOf())
                    }
                    .mapFailure { message, title -> Log.e("workoutsByGroupEvent", message) }
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



    private fun onWorkoutClicked() {
    }

    private fun getDefaultSelectedDate(): Date {
        if (!argSelectedDate.isNullOrEmpty()) {
            return argSelectedDate?.convertDefaultStringToDate() ?: Date()
        }
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
//        workoutPlanViewModel.weeklyWorkoutPlans[date]?.let { workouts ->
//            workoutAdapter.setWorkouts(workouts)
//        }
    }
}