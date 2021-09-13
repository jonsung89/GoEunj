package com.jonsung.goeunj

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jonsung.goeunj.databinding.ActivityMainBinding
import com.jonsung.goeunj.manager.FirebaseUserManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.jonsung.goeunj.data.map
import com.jonsung.goeunj.data.mapFailure
import com.jonsung.goeunj.data.model.WorkoutPlan
import com.jonsung.goeunj.utils.Constants.FirebaseCollectionPath.WORKOUT_PLANS
import com.squareup.picasso.Target


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var actionBarMenu: Menu? = null
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom Navigation View setup
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_workout, R.id.navigation_progress
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login -> {
                    supportActionBar?.hide()
                    navView.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }
            }
        }
        FirebaseUserManager.auth = FirebaseAuth.getInstance()

        mainViewModel.workoutPlanFetchEvent.observe(this, { loadingState ->
            loadingState
                .map {
                    mainViewModel.setWorkoutPlan(it)
                }
                .mapFailure { message, title ->
                    Log.e("workoutPlanFetchEvent", message)
                }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        actionBarMenu = menu
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        setProfileImage()

        // TODO: hardcoded eunji's userId - change back to `FirebaseUserManager.getCurrentUser()?.uid`
        val userId = "Ic7ycswLEeWRXuzgmpsVsoemrcI3"
        mainViewModel.fetchWorkoutPlansByUserId(userId)
    }

    fun setProfileImage() {
        FirebaseUserManager.getCurrentUser()?.let {
            Glide.with(this)
                .asBitmap().error(R.drawable.ic_baseline_account_circle_24)
                .circleCrop()
                .load(it.photoUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        actionBarMenu?.findItem(R.id.action_profile)?.icon = BitmapDrawable(resources, resource)
                    }
                })
        }
    }
}