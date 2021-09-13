package com.jonsung.goeunj.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.model.WorkoutData
import com.jonsung.goeunj.databinding.LayoutQuickViewWorkoutItemBinding

class QuickViewWorkoutAdapter(val context: Context, val onClickListener: (WorkoutData) -> Unit) : RecyclerView.Adapter<QuickViewWorkoutAdapter.ViewHolder>() {

    private var workouts: MutableList<WorkoutData> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setWorkouts(list: List<WorkoutData>) {
        workouts.clear()
        workouts = list.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = DataBindingUtil.inflate<LayoutQuickViewWorkoutItemBinding>(inflater, R.layout.layout_quick_view_workout_item, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setupUi(holder, workouts[position])
    }

    private fun setupUi(vh: ViewHolder, workout: WorkoutData) {
        with (vh.layout) {
            workoutName.text = workout.title
            sets.text = context.resources.getString(R.string.workout_sets, workout.goal_sets.toString())
            reps.text = context.resources.getString(R.string.workout_reps, workout.goal_reps.toString())
            cardView.setOnClickListener {
                onClickListener.invoke(workout)
            }
        }
    }

    override fun getItemCount(): Int = workouts.size

    class ViewHolder(val layout: LayoutQuickViewWorkoutItemBinding) : RecyclerView.ViewHolder(layout.root)
}