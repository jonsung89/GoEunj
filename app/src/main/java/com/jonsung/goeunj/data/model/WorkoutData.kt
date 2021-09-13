package com.jonsung.goeunj.data.model

import android.os.Parcelable
import com.jonsung.goeunj.model.MuscleGroup
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class WorkoutData(
    var description: String? = "",
    var goal_reps: Int? = 0,
    var goal_sets: Int? = 0,
    var is_completed: Boolean? = false,
    var muscle_group: MuscleGroup? = null,
    var name: String? = "",
    var title: String? = "",
    var weight_per_set: List<WorkoutPerSet>? = null
): Serializable, Parcelable

@Parcelize
data class WorkoutPerSet(
    var difficulty: Int? = 0,
    var total_reps_completed: Int? = 0,
    var weight: Int? = 0
): Serializable, Parcelable