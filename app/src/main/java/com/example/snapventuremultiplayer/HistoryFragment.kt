package com.example.snapventuremultiplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.ui.HistoryViewModel
import com.example.snapventuremultiplayer.ui.dashboard.FriendsViewModel

class HistoryFragment : Fragment() {
    private lateinit var historyViewModel: HistoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_history, container, false)
        setupViewBinding(root)
        return root
    }

    private fun setupViewBinding(root: View?) {

    }

    companion object {
    }
}