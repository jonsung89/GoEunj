package com.jonsung.goeunj.ui.goal_setter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonsung.goeunj.data.LoadingState
import com.jonsung.goeunj.data.model.WorkoutToBeReviewed
import com.jonsung.goeunj.data.model.WorkoutsByGroup
import com.jonsung.goeunj.repository.FirestoreRepository
import com.natpryce.map
import com.natpryce.mapFailure
import kotlinx.coroutines.launch
import java.util.*

class GoalSetterViewModel : ViewModel() {
    private val requestInitiatorId = this::class.java.simpleName
    private val firestoreRepository = FirestoreRepository()

    val selectedDays = MutableLiveData<SelectedDatesData>()
    val selectedCardioDays = MutableLiveData<SelectedDatesData>()

    val postNewGoalEvent = MutableLiveData<LoadingState<Unit>>()

    fun setSelectedDays(selectedDate: Date, isSelected: Boolean? = true) {
        val dates = selectedDays.value?.selectedDates?.toMutableList() ?: mutableListOf()

        if (isSelected == true) {
            dates.add(selectedDate)
        } else {
            dates.remove(selectedDate)
        }
        dates.sortedBy { it }
        val newDate = NewDate(selectedDate, isSelected ?: true)
        selectedDays.value = SelectedDatesData(newDate, dates)
    }

    fun setSelectedCardioDays(selectedDate: Date, isSelected: Boolean? = true) {
        val dates = selectedCardioDays.value?.selectedDates?.toMutableList() ?: mutableListOf()

        if (isSelected == true) {
            dates.add(selectedDate)
        } else {
            dates.remove(selectedDate)
        }
        dates.sortedBy { it }
        val newDate = NewDate(selectedDate, isSelected ?: true)
        selectedCardioDays.value = SelectedDatesData(newDate, dates)
    }

    fun postNewGoalToBeReviewed(userId: String, goal: WorkoutToBeReviewed) {
        viewModelScope.launch {
            firestoreRepository.postWorkoutGoalsToBeReviewed(userId, goal)
                .map {
                    postNewGoalEvent.value = LoadingState.Loaded(Unit, requestInitiatorId)
                }
                .mapFailure {
                    Log.e("postNewGoal", it.localizedMessage ?: "error")
                    postNewGoalEvent.value = LoadingState.Failed(it.localizedMessage ?: "", requestInitiatorId)
                }
        }
    }

    data class SelectedDatesData(
        val newSelectedDate: NewDate,
        val selectedDates: List<Date>
    )

    data class NewDate(
        val selectedDate: Date,
        val isSelected: Boolean
    )
}