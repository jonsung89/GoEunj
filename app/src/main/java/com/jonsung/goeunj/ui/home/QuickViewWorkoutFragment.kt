package com.jonsung.goeunj.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonsung.goeunj.MainViewModel
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.map
import com.jonsung.goeunj.data.mapFailure
import com.jonsung.goeunj.data.model.WorkoutByDate
import com.jonsung.goeunj.data.model.WorkoutData
import com.jonsung.goeunj.databinding.LayoutQuckViewWorkoutBinding
import com.jonsung.goeunj.ui.workout_plan.WorkoutDetailDialogFragment
import com.jonsung.goeunj.utils.convertDateToString


class QuickViewWorkoutFragment : Fragment() {
    private var workoutByDate: WorkoutByDate? = null
    private lateinit var layout: LayoutQuckViewWorkoutBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels ({ requireParentFragment() })
    private var workoutsAdapter: QuickViewWorkoutAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workoutByDate = it.getParcelable(ARG_WORKOUT_BY_DATE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layout = DataBindingUtil.inflate(inflater, R.layout.layout_quck_view_workout, container, false)

        workoutsAdapter = QuickViewWorkoutAdapter(requireContext()) {
            handleWorkoutOnClick(it)
        }

        layout.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = workoutsAdapter
        }

        with (mainViewModel) {
            workoutsByGroupEvent.observe(requireActivity(), { loadingState ->
                loadingState
                    .map {
                        Log.d("workoutsByGroupEvent", "workoutsByGroup: $it")
                        workoutsAdapter?.setWorkouts(it.workouts ?: listOf())
                    }
                    .mapFailure { message, title -> Log.e("workoutsByGroupEvent", message) }
            })
        }

        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fetch all workouts for the selected date
        workoutByDate?.muscleGroup?.let {
            // TODO: hardcoded eunji's userId - change back to `FirebaseUserManager.getCurrentUser()?.uid`
            val userId = "Ic7ycswLEeWRXuzgmpsVsoemrcI3"
            mainViewModel.fetchWorkoutsByGroupByUserId(userId, it)
        }

        with (layout) {
            date.text = workoutByDate?.date?.convertDateToString()
            muscleGroupName.text = workoutByDate?.muscleGroup?.title

            quickViewClose.setOnClickListener {
                homeViewModel.setQuickWorkoutView(null)
            }

            detailViewButton.setOnClickListener {
                workoutByDate?.date?.let {
                    val bundle = bundleOf("selectedWorkoutDate" to it.toString())
                    findNavController().navigate(R.id.navigation_workout, bundle)
                }
            }
        }
    }

    private fun handleWorkoutOnClick(workout: WorkoutData) {
        activity?.supportFragmentManager?.let {
            WorkoutDetailDialogFragment.newInstance(workout).apply {
                isCancelable = false
                show(it, TAG)
            }
        }
    }

    companion object {
        private const val ARG_WORKOUT_BY_DATE = "arg_workout_by_date"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param workoutByDate WorkoutByDate.
         * @return A new instance of fragment QuckViewWorkoutDialogFragment.
         */
        fun newInstance(workoutByDate: WorkoutByDate) =
                QuickViewWorkoutFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_WORKOUT_BY_DATE, workoutByDate)
                    }
                }
    }
}