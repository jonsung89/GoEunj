package com.jonsung.goeunj.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class WorkoutToBeReviewed(
    val cardioDays: List<Date>? = null,
    val displayName: String? = null,
    val isReviewed: Boolean? = null,
    val startWeek: Date? = null,
    val userId: String? = null,
    val workoutDays: List<Date>? = listOf(),
): Parcelable
