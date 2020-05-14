package com.example.snapventuremultiplayer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.ui.dashboard.FriendsViewModel
import com.example.snapventuremultiplayer.ui.home.HomeViewModel

class FriendsFragment : Fragment() {
    private lateinit var friendsViewModel: FriendsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        friendsViewModel =
            ViewModelProvider(this).get(FriendsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_friends, container, false)
        setupViewBinding(root)
        return root
    }

    private fun setupViewBinding(root: View?) {

    }

    companion object {
    }
}