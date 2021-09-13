package com.jonsung.goeunj.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import com.jonsung.goeunj.MainViewModel
import com.jonsung.goeunj.R
import com.jonsung.goeunj.data.map
import com.jonsung.goeunj.data.mapFailure
import com.jonsung.goeunj.data.model.WorkoutByDate
import com.jonsung.goeunj.databinding.FragmentHomeBinding
import com.jonsung.goeunj.databinding.LayoutDailyWorkoutPlanBinding
import com.jonsung.goeunj.manager.FirebaseUserManager
import com.jonsung.goeunj.utils.getDayOfMonth
import com.jonsung.goeunj.utils.isDateInCurrentWeek
import com.jonsung.goeunj.utils.isDayOfWeek
import com.jonsung.goeunj.utils.isSameDay
import java.util.*

class HomeFragment : Fragment() {

    companion object {
        const val TAG = "fragment_home"
    }

    private lateinit var layout: FragmentHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var currentlySelectedDateView: LayoutDailyWorkoutPlanBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        activity?.actionBar?.title = resources.getString(R.string.app_name)

        with (mainViewModel) {
            workoutPlanEvent.observe(requireActivity(), { plan ->
                plan.goals?.let { goals ->
                    goals.firstOrNull { it.startWeek?.isDateInCurrentWeek() == true }?.let {
                        Log.d(TAG, "workoutPlan for the week: $it")
                        layout.weeklyCalendarCard.visibility = View.VISIBLE
                        layout.setGoalButton.visibility = View.GONE
                        mainViewModel.setWorkoutPlansByWeek(it)
                    } ?: run {
                        Log.d(TAG, "workoutPlan not found")

                        // TODO if not found, see if user has already submitted a review
                        // TODO: hardcoded eunji's userId - change back to `FirebaseUserManager.getCurrentUser()?.uid`
                        val userId = "Ic7ycswLEeWRXuzgmpsVsoemrcI3"
                        mainViewModel.fetchWorkoutGoalsToBeReviewed(userId)

                        with (layout) {
                            weeklyCalendarCard.visibility = View.GONE
                            setGoalButton.visibility = View.VISIBLE
                            setGoalButton.setOnClickListener {
                                // TODO set goal
                                findNavController().navigate(R.id.navigation_set_goal)
                            }
                        }
                    }
                }
            })

            workoutsToBeReviewedFetchEvent.observe(requireActivity(), { loadingState ->
                loadingState
                    .map { workoutToBeReviewed ->
                        if (workoutToBeReviewed == null
                            || workoutToBeReviewed.isReviewed == true
                            || (workoutToBeReviewed.isReviewed == false && workoutToBeReviewed.startWeek?.isDateInCurrentWeek() == false) ) {
                            with (layout) {
                                weeklyCalendarCard.visibility = View.GONE
                                setGoalButton.visibility = View.VISIBLE
                                layout.planBeingReviewedGroup.visibility = View.GONE
                                layout.setGoalButton.isEnabled = true
                                setGoalButton.setOnClickListener {
                                    // TODO set goal
                                    findNavController().navigate(R.id.navigation_set_goal)
                                }
                            }
                        } else {
                            layout.planBeingReviewedGroup.visibility = View.VISIBLE
                            layout.setGoalButton.isEnabled = false
                        }
                    }
                    .mapFailure { message, title -> Log.e("workoutsToBeReviewedFetchEvent", message) }
            })

            workoutPlansByWeekEvent.observe(requireActivity(), {
                if (isAdded) {
                    setWeeklyCalendar(it)
                }
            })

            workoutPlanSubmitted.observe(requireActivity(), {
                layout.setGoalButton.isEnabled = !it
                layout.planBeingReviewedGroup.visibility = if (it) View.VISIBLE else View.GONE
            })
        }

        with (homeViewModel) {
            quickWorkoutViewEvent.observe(requireActivity(), { workoutByDate ->
                if (workoutByDate == null) {
                    Log.d("quickWorkoutViewEvent", "null")
                    currentlySelectedDateView?.overlay?.visibility = View.GONE
                    currentlySelectedDateView = null
                    with (layout.quickViewCardView) {
                        visibility = View.GONE
                        removeAllViews()
                    }
                } else {
                    Log.d("quickWorkoutViewEvent", "${workoutByDate.date}")
                    childFragmentManager.commit {
                        setReorderingAllowed(true)
                        val quickViewWorkoutFragment = QuickViewWorkoutFragment.newInstance(workoutByDate)
                        replace(R.id.quickViewCardView, quickViewWorkoutFragment, "quick_view_workout_fragment")
                    }
                    layout.quickViewCardView.visibility = View.VISIBLE
                }
            })
        }

        return layout.root
    }

    override fun onResume() {
        super.onResume()

        auth.currentUser?.let { firebaseUser ->
            FirebaseUserManager.setUserAccount(firebaseUser)
            updateUI()
        } ?: run {
            findNavController().navigate(R.id.navigation_login)
        }
    }

    private fun updateUI() {
        with (layout) {
            FirebaseUserManager.getUserAccount()?.displayName?.let {
                greetingHi.text = resources.getString(R.string.greeting_hi_user, it)
            }

            workoutButton.setOnClickListener {
                Log.d("selectedDate", Date().toString())
                val bundle = bundleOf("selectedWorkoutDate" to Date().toString())
                findNavController().navigate(R.id.navigation_workout, bundle)
            }

            progressButton.setOnClickListener {
                findNavController().navigate(R.id.navigation_progress)
            }
        }
    }

    private fun setWeeklyCalendar(workouts: Map<Date?, WorkoutByDate>) {
        layout.weeklyCalendar.apply {
            with (sundayView) {
                day.text = "S"
                workoutForTheDay(Calendar.SUNDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }

            with (mondayView) {
                day.text = "M"
                workoutForTheDay(Calendar.MONDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }

            with (tuesdayView) {
                day.text = "T"
                workoutForTheDay(Calendar.TUESDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }

            with (wednesdayView) {
                day.text = "W"
                workoutForTheDay(Calendar.WEDNESDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }

            with (thursdayView) {
                day.text = "T"
                workoutForTheDay(Calendar.THURSDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }

            with (fridayView) {
                day.text = "F"
                workoutForTheDay(Calendar.FRIDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }

            with (saturdayView) {
                day.text = "S"
                workoutForTheDay(Calendar.SATURDAY)?.let {
                    weeklyCalendarUiUpdate(this, it)
                }
            }
        }
    }

    private fun workoutForTheDay(dayOfWeek: Int): WorkoutByDate? {
        return mainViewModel.workoutGoal.firstOrNull { it.date?.isDayOfWeek(dayOfWeek) == true } ?: run { null }
    }

    private fun weeklyCalendarUiUpdate(view: LayoutDailyWorkoutPlanBinding, workoutByDate: WorkoutByDate) {
        with (view) {
            date.text = workoutByDate.date?.getDayOfMonth().toString()
            workout.text = workoutByDate.muscleGroup?.title

            workoutIcon.visibility = if (workoutByDate.muscleGroup?.isWorkout() == true) View.VISIBLE else View.INVISIBLE

            constraintLayout.setOnClickListener {
                if (currentlySelectedDateView != null) currentlySelectedDateView?.overlay?.visibility = View.GONE
                currentlySelectedDateView = view
                currentlySelectedDateView?.overlay?.visibility = View.VISIBLE
                handleWorkoutDateOnClick(workoutByDate)
            }


            workoutByDate.date?.let {
                if (Date().isSameDay(it)) {
                    val bgColor = R.color.half_baked_500
                    dateBg.setCardBackgroundColor(context?.getColorStateList(bgColor))
                    date.setTextColor(resources.getColor(R.color.white, requireContext().theme))
                } else {
                    dateBg.setCardBackgroundColor(null)
                    date.setTextColor(MaterialColors.getColor(this.root, R.attr.colorOnSurface))
                }
            }
        }
    }

    private fun handleWorkoutDateOnClick(workoutByDate: WorkoutByDate) {
        homeViewModel.setQuickWorkoutView(workoutByDate)
    }

}