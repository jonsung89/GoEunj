package com.jonsung.goeunj.ui.workout_plan

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonsung.goeunj.data.LoadingState
import com.jonsung.goeunj.data.model.WeekGoal
import com.jonsung.goeunj.data.model.WorkoutByDate
import com.jonsung.goeunj.data.model.WorkoutPlan
import com.jonsung.goeunj.data.model.WorkoutsByGroup
import com.jonsung.goeunj.model.MuscleGroup
import com.jonsung.goeunj.model.Workout
import com.jonsung.goeunj.model.Workout.*
import com.jonsung.goeunj.repository.FirestoreRepository
import com.natpryce.map
import com.natpryce.mapFailure
import kotlinx.coroutines.launch
import java.util.*


class WorkoutPlanViewModel : ViewModel() {
    private val requestInitiatorId = this::class.java.simpleName
    private val firestoreRepository = FirestoreRepository()

    val selectedDate: MutableLiveData<Date> = MutableLiveData()

    var workoutPlanFetchEvent = MutableLiveData<LoadingState<WorkoutPlan>>()
    var workoutPlansByWeekEvent: MutableLiveData<Map<Date?, WorkoutByDate>> = MutableLiveData()

    var workoutsByGroupEvent = MutableLiveData<LoadingState<WorkoutsByGroup>>()

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

    fun fetchWorkoutPlansByUserId(userId: String) {
        viewModelScope.launch {
            firestoreRepository.getWorkoutPlansByUserId(userId)
                .map {
                    for (document in it) {
                        Log.d("fetchWorkoutPlansByUserId", "${document.id} => ${document.data}")
                        val plan = document.toObject(WorkoutPlan::class.java)
                        workoutPlanFetchEvent.value = LoadingState.Loaded(plan, requestInitiatorId)
                    }
                }
                .mapFailure {
                    workoutPlanFetchEvent.value = LoadingState.Failed(it.localizedMessage ?: "", requestInitiatorId)
                }
        }
    }

    fun setWorkoutPlansByWeek(weekGoal: WeekGoal) {
        weekGoal.workoutForTheWeek?.map { it.date to it }?.toMap()?.let {
            workoutPlansByWeekEvent.postValue(it)
        }
    }

    fun fetchWorkoutsByGroupByUserId(userId: String, muscleGroup: MuscleGroup) {
        viewModelScope.launch {
            firestoreRepository.getWorkoutsByGroupByUserId(userId, muscleGroup)
                .map {
                    for (document in it) {
                        Log.d("fetchWorkoutsByGroupByUserId", "${document.id} => ${document.data}")
                        val plan = document.toObject(WorkoutsByGroup::class.java)
                        workoutsByGroupEvent.value = LoadingState.Loaded(plan, requestInitiatorId)
                    }
                }
                .mapFailure {
                    workoutsByGroupEvent.value = LoadingState.Failed(it.localizedMessage ?: "", requestInitiatorId)
                }
        }
    }
}