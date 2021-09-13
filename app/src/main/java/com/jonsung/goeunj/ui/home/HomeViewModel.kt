package com.jonsung.goeunj.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jonsung.goeunj.data.model.WorkoutByDate

class HomeViewModel : ViewModel() {

    val quickWorkoutViewEvent: MutableLiveData<WorkoutByDate?> = MutableLiveData()

    fun setQuickWorkoutView(workoutByDate: WorkoutByDate?) {
        if (workoutByDate == null || workoutByDate.date == quickWorkoutViewEvent.value?.date) {
            quickWorkoutViewEvent.value = null
        } else {
            quickWorkoutViewEvent.value = workoutByDate
        }
    }
}