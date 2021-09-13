package com.jonsung.goeunj.ui.goal_setter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.jonsung.goeunj.MainViewModel
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.map
import com.jonsung.goeunj.data.mapFailure
import com.jonsung.goeunj.data.model.WeekGoal
import com.jonsung.goeunj.data.model.WorkoutByDate
import com.jonsung.goeunj.data.model.WorkoutToBeReviewed
import com.jonsung.goeunj.databinding.FragmentGoalSetterBinding
import com.jonsung.goeunj.databinding.LayoutDailyWorkoutPlanBinding
import com.jonsung.goeunj.ui.home.HomeViewModel
import com.jonsung.goeunj.utils.*
import java.util.*


class GoalSetterFragment : DialogFragment() {
    private lateinit var layout: FragmentGoalSetterBinding
    private val goalSetterViewModel: GoalSetterViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var datesCurrentWeek: List<String?> = listOf()

    override fun getTheme(): Int {
        return R.style.DialogFragmentFullScreenTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_goal_setter, container, false)

        with (goalSetterViewModel) {
            selectedDays.observe(viewLifecycleOwner, {
                updateWorkoutDateIndicator(it.newSelectedDate.selectedDate, it.newSelectedDate.isSelected)
                updateWorkoutDaysCount(it.selectedDates)

                layout.confirmWorkoutButton.isEnabled = it.selectedDates.size >= 4
                layout.confirmButton.isEnabled = it.selectedDates.size >= 4

            })
            selectedCardioDays.observe(viewLifecycleOwner, {
                updateCardioDayIndicator(it.newSelectedDate.selectedDate, it.newSelectedDate.isSelected)
                updateCardioDaysCount(it.selectedDates)
            })

            postNewGoalEvent.observe(requireActivity(), { loadingState ->
                loadingState
                    .map {
                        //TODO green snackbar
                        mainViewModel.workoutPlanSubmitted.value = true
                        Snackbar.make(layout.root, "Submitted Successfully!", Snackbar.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            dismiss()
                        }, 2000)
                    }
                    .mapFailure { message, title -> Log.e("postNewGoalEvent", message) }
            })
        }

        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (layout) {
            closeButton.setOnClickListener { dismiss() }
            selectedDayCount.text = resources.getString(R.string.selectedDaysCount, 0, 4)
            selectedCardioDayCount.text = resources.getString(R.string.selectedDaysCount, 0, 7)
            cardioGroup.visibility = View.GONE

            // Confirm Workout
            confirmWorkoutButton.setOnClickListener {
                layout.cardioGroup.visibility = View.VISIBLE
                layout.editWorkoutButton.visibility = View.VISIBLE
                confirmWorkoutButton.isEnabled = false
            }

            // Edit Workout
            editWorkoutButton.setOnClickListener {
                layout.cardioGroup.visibility = View.GONE
                confirmWorkoutButton.isEnabled = true
            }

            // Confirm goal
            confirmButton.setOnClickListener {
                // TODO: hardcoded eunji's userId - change back to `FirebaseUserManager.getCurrentUser()?.uid`
                // TODO: displayName
                val userId = "Ic7ycswLEeWRXuzgmpsVsoemrcI3"

                val goalToBeReviewed = WorkoutToBeReviewed(
                    cardioDays = goalSetterViewModel.selectedCardioDays.value?.selectedDates ?: listOf(),
                    displayName = "Eunji Kim",
                    isReviewed = false,
                    startWeek = datesCurrentWeek[0]?.convertStringToDate(DEFAULT_DATE_PATTERN) ?: Date(),
                    userId = userId,
                    workoutDays = goalSetterViewModel.selectedDays.value?.selectedDates ?: listOf()
                )
                confirmButton.isEnabled = false
                goalSetterViewModel.postNewGoalToBeReviewed(userId, goalToBeReviewed)
            }
        }

        setWeeklyCalendar()
    }

    private fun setWeeklyCalendar() {
        layout.weeklyCalendar.apply {

            datesCurrentWeek = getAllDatesOfCurrentWeek()

            with (sundayView) {
                day.text = "S"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[0])
            }

            with (mondayView) {
                day.text = "M"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[1])
            }

            with (tuesdayView) {
                day.text = "T"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[2])
            }

            with (wednesdayView) {
                day.text = "W"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[3])
            }

            with (thursdayView) {
                day.text = "T"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[4])
            }

            with (fridayView) {
                day.text = "F"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[5])
            }

            with (saturdayView) {
                day.text = "S"
                weeklyCalendarUiUpdate(this, datesCurrentWeek[6])
            }
        }
    }

    private fun weeklyCalendarUiUpdate(view: LayoutDailyWorkoutPlanBinding, dateStr: String?) {
        with (view) {
            val convertedDate = dateStr?.convertStringToDate(DEFAULT_DATE_PATTERN)

            date.text = convertedDate?.getDayOfMonth().toString()

            workoutIcon.visibility = View.GONE

            convertedDate?.let {
                if (Date().isSameDay(it)) {
                    val bgColor = R.color.half_baked_500
                    dateBg.setCardBackgroundColor(context?.getColorStateList(bgColor))
                    date.setTextColor(resources.getColor(R.color.white, requireContext().theme))
                } else {
                    dateBg.setCardBackgroundColor(null)
                    date.setTextColor(MaterialColors.getColor(this.root, R.attr.colorOnSurface))
                }
            }

            constraintLayout.setOnClickListener {
                convertedDate?.let {
                    if (layout.cardioGroup.visibility == View.GONE) {
                        val isSelected = view.workoutIcon.visibility == View.GONE
                        goalSetterViewModel.setSelectedDays(it, isSelected)
                    } else {
                        val isSelected = view.cardioIcon.visibility == View.GONE
                        goalSetterViewModel.setSelectedCardioDays(it, isSelected)
                    }
                }
            }
        }
    }

    private fun updateWorkoutDaysCount(totalDates: List<Date>) {
        layout.selectedDayCount.text = resources.getString(R.string.selectedDaysCount, totalDates.size, 4)
    }

    private fun updateCardioDaysCount(totalDates: List<Date>) {
        layout.selectedCardioDayCount.text = resources.getString(R.string.selectedDaysCount, totalDates.size, 7)
    }

    private fun updateWorkoutDateIndicator(date: Date, isSelected: Boolean) {
        with (layout.weeklyCalendar) {
            val workoutDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_gym, context?.theme)
            when (date.getDayOfWeek()) {
                Calendar.SUNDAY -> {
                    sundayView.workoutIcon.setImageDrawable(workoutDrawable)
                    sundayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.MONDAY -> {
                    mondayView.workoutIcon.setImageDrawable(workoutDrawable)
                    mondayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.TUESDAY -> {
                    tuesdayView.workoutIcon.setImageDrawable(workoutDrawable)
                    tuesdayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.WEDNESDAY -> {
                    wednesdayView.workoutIcon.setImageDrawable(workoutDrawable)
                    wednesdayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.THURSDAY -> {
                    thursdayView.workoutIcon.setImageDrawable(workoutDrawable)
                    thursdayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.FRIDAY -> {
                    fridayView.workoutIcon.setImageDrawable(workoutDrawable)
                    fridayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.SATURDAY -> {
                    saturdayView.workoutIcon.setImageDrawable(workoutDrawable)
                    saturdayView.workoutIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun updateCardioDayIndicator(date: Date, isSelected: Boolean) {
        with (layout.weeklyCalendar) {
            when (date.getDayOfWeek()) {
                Calendar.SUNDAY -> {
                    sundayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.MONDAY -> {
                    mondayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.TUESDAY -> {
                    tuesdayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.WEDNESDAY -> {
                    wednesdayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.THURSDAY -> {
                    thursdayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.FRIDAY -> {
                    fridayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                Calendar.SATURDAY -> {
                    saturdayView.cardioIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
            }
        }
    }

}