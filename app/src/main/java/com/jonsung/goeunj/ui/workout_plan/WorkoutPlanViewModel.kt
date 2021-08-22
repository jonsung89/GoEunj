package com.jonsung.goeunj.ui.workout_plan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jonsung.goeunj.model.Workout
import com.jonsung.goeunj.model.Workout.*
import java.util.*


class WorkoutPlanViewModel : ViewModel() {

    val selectedDate: MutableLiveData<Date> = MutableLiveData()

    // hardcoded for now
    val hardcodedWeek = listOf("08/23/2021","08/24/2021","08/25/2021","08/26/2021","08/27/2021","08/28/2021")

    val weeklyWorkoutPlans = hashMapOf<String, List<Workout>>()


    init {
        for (date in hardcodedWeek) {
            when (date) {
                "08/23/2021" -> {
                    val workouts = listOf(BARBELL_SQUAT, LEG_PRESS, LEG_EXTENSION, LEG_CURL, DUMBBELL_GOBLET_SQUAT, DUMBBELL_STIFF_LEG_DEADLIFT)
                    weeklyWorkoutPlans[date] = workouts
                }
                "08/24/2021" -> {
                    val workouts = listOf(BARBELL_SQUAT, LEG_PRESS, LEG_EXTENSION, LEG_CURL, DUMBBELL_GOBLET_SQUAT, DUMBBELL_STIFF_LEG_DEADLIFT)
                    weeklyWorkoutPlans[date] = workouts
                }
                "08/25/2021" -> {
                    weeklyWorkoutPlans[date] = listOf()
                }
                "08/26/2021" -> {
                    val workouts = listOf(BARBELL_SQUAT, LEG_PRESS, LEG_EXTENSION, LEG_CURL, DUMBBELL_GOBLET_SQUAT, DUMBBELL_STIFF_LEG_DEADLIFT)
                    weeklyWorkoutPlans[date] = workouts
                }
                "08/27/2021" -> {
                    val workouts = listOf(BARBELL_SQUAT, LEG_PRESS, LEG_EXTENSION, LEG_CURL, DUMBBELL_GOBLET_SQUAT, DUMBBELL_STIFF_LEG_DEADLIFT)
                    weeklyWorkoutPlans[date] = workouts
                }
                "08/28/2021" -> {
                    weeklyWorkoutPlans[date] = listOf()
                }
            }
        }
    }

    fun setSelectedDate(date: Date) {
        selectedDate.value = date
    }

}