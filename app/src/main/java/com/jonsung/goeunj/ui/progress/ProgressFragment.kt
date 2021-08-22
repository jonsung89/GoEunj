package com.jonsung.goeunj.ui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jonsung.goeunj.R
import com.jonsung.goeunj.databinding.FragmentProgressBinding

class ProgressFragment : Fragment() {

    private lateinit var progressViewModel: ProgressViewModel
    private lateinit var layout: FragmentProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        progressViewModel =
            ViewModelProvider(this).get(ProgressViewModel::class.java)

        layout = DataBindingUtil.inflate(inflater, R.layout.fragment_progress, container, false)

        return layout.root
    }

}