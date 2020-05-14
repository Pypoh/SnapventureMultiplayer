package com.example.snapventuremultiplayer.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.repository.model.QuestionsModel
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.loading.LoadingMatchActivity
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.Result.Companion.failure
import kotlin.collections.ArrayList
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    lateinit var matchTestButton: MaterialButton
//    lateinit var btn_vs1: ImageButton
//    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
//
//    var roomIds: MutableList<String> = ArrayList()
//    var questionIds: MutableList<String> = ArrayList()
//    val questionDataSet: ArrayList<QuestionsModel> = ArrayList()

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