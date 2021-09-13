package com.jonsung.goeunj

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jonsung.goeunj.data.LoadingState
import com.jonsung.goeunj.data.model.*
import com.jonsung.goeunj.model.MuscleGroup
import com.jonsung.goeunj.repository.FirestoreRepository
import com.natpryce.map
import com.natpryce.mapFailure
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel : ViewModel() {
    private val requestInitiatorId = this::class.java.simpleName
    private val firestoreRepository = FirestoreRepository()

    var workoutPlanFetchEvent = MutableLiveData<LoadingState<WorkoutPlan>>()
    var workoutsByGroupEvent = MutableLiveData<LoadingState<WorkoutsByGroup>>()

    var workoutsToBeReviewedFetchEvent = MutableLiveData<LoadingState<WorkoutToBeReviewed?>>()

    var workoutPlanEvent: MutableLiveData<WorkoutPlan> = MutableLiveData()

    var workoutGoal = listOf<WorkoutByDate>()
    var workoutPlansByWeekEvent: MutableLiveData<Map<Date?, WorkoutByDate>> = MutableLiveData()
    var workoutPlanSubmitted: MutableLiveData<Boolean> = MutableLiveData(false)

    fun setWorkoutPlan(plan: WorkoutPlan) {
        workoutPlanEvent.postValue(plan)
    }

    fun setWorkoutPlansByWeek(weekGoal: WeekGoal) {
        workoutGoal = weekGoal.workoutForTheWeek?.sortedBy { it.date } ?: listOf()

        weekGoal.workoutForTheWeek?.map { it.date to it }?.toMap()?.let {
            workoutPlansByWeekEvent.postValue(it)
        }
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

    fun fetchWorkoutsByGroupByUserId(userId: String, muscleGroup: MuscleGroup) {
        viewModelScope.launch {
            firestoreRepository.getWorkoutsByGroupByUserId(userId, muscleGroup)
                .map {
                    if (it.isEmpty) {
                        Log.d("fetchWorkoutsByGroupByUserId", "empty")
                        workoutsByGroupEvent.value = LoadingState.Loaded(WorkoutsByGroup(), requestInitiatorId)
                    }
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

    fun fetchWorkoutGoalsToBeReviewed(userId: String) {
        viewModelScope.launch {
            firestoreRepository.getWorkoutGoalsToBeReviewed(userId)
                .map {
                    if (it.isEmpty) {
                        Log.d("fetchWorkoutGoalsToBeReviewed", "empty")
                        workoutsToBeReviewedFetchEvent.value = LoadingState.Loaded(null, requestInitiatorId)
                    }
                    for (document in it) {
                        Log.d("fetchWorkoutGoalsToBeReviewed", "${document.id} => ${document.data}")
                        val planToBeReviewed = document.toObject(WorkoutToBeReviewed::class.java)
                        workoutsToBeReviewedFetchEvent.value = LoadingState.Loaded(planToBeReviewed, requestInitiatorId)
                    }
                }
                .mapFailure {
                    workoutsToBeReviewedFetchEvent.value = LoadingState.Failed(it.localizedMessage ?: "", requestInitiatorId)
                }
        }
    }
}