package com.jonsung.goeunj.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jonsung.goeunj.R
import com.jonsung.goeunj.databinding.FragmentHomeBinding
import com.jonsung.goeunj.manager.FirebaseUserManager

class HomeFragment : Fragment() {

    companion object {
        const val TAG = "fragment_home"
    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var layout: FragmentHomeBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        activity?.actionBar?.title = resources.getString(R.string.app_name)

        return layout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                findNavController().navigate(R.id.navigation_workout)
            }

            progressButton.setOnClickListener {
                findNavController().navigate(R.id.navigation_progress)
            }
        }


    }

}