package com.example.snapventuremultiplayer.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.ui.loading.LoadingMatchActivity
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    lateinit var matchTestButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        matchTestButton = root.findViewById(R.id.match_test_btn)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        matchTestButton.setOnClickListener {
            intentToLoadingMatch()
        }
    }

    private fun intentToLoadingMatch() {
        startActivity(Intent(context, LoadingMatchActivity::class.java))
    }
}