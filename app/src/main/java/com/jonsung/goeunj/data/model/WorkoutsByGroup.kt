package com.jonsung.goeunj.data.model

import android.os.Parcelable
import com.jonsung.goeunj.model.MuscleGroup
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class WorkoutsByGroup(
    var id: String? = null,
    var muscle_group: MuscleGroup? = null,
    var participantId: String? = null,
    var workouts: List<WorkoutData>? = null
): Serializable, Parcelable
