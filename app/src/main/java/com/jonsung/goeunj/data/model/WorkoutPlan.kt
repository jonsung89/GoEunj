package com.jonsung.goeunj.data.model

import android.os.Parcelable
import com.jonsung.goeunj.model.MuscleGroup
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class WorkoutPlan(
    var createdAt: Date? = null,
    var endDate: Date? = null,
    var id: String? = null,
    var participantId: String? = null,
    var planLengthInWeeks: Int? = 0,
    var splits: List<MuscleGroup>? = null, // TODO
    var startDate: Date? = null,
    var goals: List<WeekGoal>? = listOf()
): Serializable, Parcelable

@Parcelize
data class WeekGoal(
    var startWeek: Date? = null, // hopefully every sunday
    var isComplete: Boolean? = false,
    var workoutForTheWeek: List<WorkoutByDate>? = null
): Serializable, Parcelable

@Parcelize
data class WorkoutByDate(
    var date: Date? = null,
    var isComplete: Boolean? = false,
    var muscleGroup: MuscleGroup? = null
): Serializable, Parcelable