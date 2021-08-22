package com.jonsung.goeunj.ui.workout_plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jonsung.goeunj.R
import com.jonsung.goeunj.databinding.ViewWorkoutBinding
import com.jonsung.goeunj.model.Workout


class WorkoutPlanAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var workouts = mutableListOf<Workout>()

    @JvmName("setWorkouts1")
    fun setWorkouts(workoutList: List<Workout>) {
        workouts.clear()
        workouts = workoutList.toMutableList()
        notifyDataSetChanged()
    }

    fun swapWorkoutOrders(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                workouts[i] = workouts.set(i+1, workouts[i]);
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                workouts[i] = workouts.set(i-1, workouts[i]);
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int = workouts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout: ViewWorkoutBinding = DataBindingUtil.inflate(inflater, R.layout.view_workout, parent, false)
        return WorkoutViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        displayWorkout(workouts[position], holder as WorkoutViewHolder)
    }

    private fun displayWorkout(workout: Workout, viewHolder: WorkoutViewHolder) {
        with (viewHolder.layout) {
            workoutName.text = workout.title
            sets.text = "3 SETS x 12 REPS"

            cardView.setOnClickListener {
                // TODO
            }
        }
    }

    class WorkoutViewHolder(val layout: ViewWorkoutBinding) : RecyclerView.ViewHolder(layout.root)
}