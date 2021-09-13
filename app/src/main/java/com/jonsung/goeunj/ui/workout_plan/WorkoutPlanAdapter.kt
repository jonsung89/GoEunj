package com.jonsung.goeunj.ui.workout_plan

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.model.WorkoutData
import com.jonsung.goeunj.databinding.ViewWorkoutBinding
import com.jonsung.goeunj.model.Workout


class WorkoutPlanAdapter(val context: Context, val clickListener: () -> Unit?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    var workouts = mutableListOf<Workout>()

    var workoutList = mutableListOf<WorkoutData>()

//    @JvmName("setWorkouts1")
//    fun setWorkouts(workoutList: List<Workout>) {
//        workouts.clear()
//        workouts = workoutList.toMutableList()
//        notifyDataSetChanged()
//    }

    @JvmName("setWorkoutList1")
    @SuppressLint("NotifyDataSetChanged")
    fun setWorkoutList(workouts: List<WorkoutData>) {
        workoutList.clear()
        workoutList = workouts.toMutableList()
        notifyDataSetChanged()
    }

    fun swapWorkoutOrders(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                workoutList[i] = workoutList.set(i+1, workoutList[i]);
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                workoutList[i] = workoutList.set(i-1, workoutList[i]);
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int = workoutList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout: ViewWorkoutBinding = DataBindingUtil.inflate(inflater, R.layout.view_workout, parent, false)
        return WorkoutViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        displayWorkout(workoutList[position], holder as WorkoutViewHolder)
    }

    private fun displayWorkout(workout: WorkoutData, viewHolder: WorkoutViewHolder) {
        with (viewHolder.layout) {
            workoutName.text = workout.title

            val sets = context.resources.getString(R.string.workout_sets, workout.goal_sets.toString()).uppercase()
            val reps = context.resources.getString(R.string.workout_reps, workout.goal_reps.toString()).uppercase()
            val setsAndRepsString = "$sets x $reps"
            setsAndReps.text = setsAndRepsString

            cardView.setOnClickListener {
                // TODO
                clickListener.invoke()
            }
        }
    }

    class WorkoutViewHolder(val layout: ViewWorkoutBinding) : RecyclerView.ViewHolder(layout.root)
}