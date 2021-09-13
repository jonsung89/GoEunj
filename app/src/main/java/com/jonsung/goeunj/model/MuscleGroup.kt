package com.jonsung.goeunj.model

import com.google.gson.annotations.SerializedName

enum class MuscleGroup(val title: String) {
    @SerializedName("legs")
    legs("Legs"),
    @SerializedName("back")
    back("Back"),
    @SerializedName("chest")
    chest("Chest"),
    @SerializedName("biceps")
    biceps("Biceps"),
    @SerializedName("triceps")
    triceps("Triceps"),
    @SerializedName("shoulders")
    shoulders("Shoulders"),
    @SerializedName("abs")
    abs("Abs"),
    @SerializedName("quads")
    quads("Quads"),
    @SerializedName("hamstrings")
    hamstrings("Hamstrings"),

    @SerializedName("back_biceps")
    back_biceps("Back & Biceps"),
    @SerializedName("chest_triceps")
    chest_triceps("Chest & Triceps"),
    @SerializedName("shoulders_abs")
    shoulders_abs("Shoulder & Abs"),

    @SerializedName("cardio")
    cardio("Cardio"),
    @SerializedName("rest")
    rest("Rest Day");

    fun isWorkout(): Boolean {
        return this != cardio && this != rest
    }
}