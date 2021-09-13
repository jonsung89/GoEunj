package com.jonsung.goeunj.ui.workout_plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.model.WorkoutData
import com.jonsung.goeunj.databinding.FragmentWorkoutDetailDialogBinding
import com.jonsung.goeunj.utils.setWidthPercent

class WorkoutDetailDialogFragment : DialogFragment() {
    val TAG = javaClass::class.java.simpleName
    private lateinit var layout: FragmentWorkoutDetailDialogBinding
    private var workout: WorkoutData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workout = it.getParcelable(ARG_WORKOUT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_workout_detail_dialog, container, false)

        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (layout) {
            workoutName.text = workout?.title
            workoutDescription.text = workout?.description

            close.setOnClickListener { dismiss() }
        }
    }

    override fun onResume() {
        super.onResume()
        setWidthPercent(100)
    }

    companion object {
        private const val ARG_WORKOUT = "arg_workout"

        fun newInstance(workout: WorkoutData) =
            WorkoutDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_WORKOUT, workout)
                }
            }
    }
}