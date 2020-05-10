package com.example.snapventuremultiplayer.ui.loading

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.repository.datasource.remote.auth.login.LoginRepoImpl
import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.auth.AuthActivity
import com.example.snapventuremultiplayer.ui.auth.login.LoginVMFactory
import com.example.snapventuremultiplayer.ui.auth.login.LoginViewModel
import com.example.snapventuremultiplayer.ui.auth.login.domain.LoginImpl
import com.example.snapventuremultiplayer.ui.camera.CameraActivity
import com.example.snapventuremultiplayer.ui.loading.domain.LoadingMatchImpl
import com.example.snapventuremultiplayer.utils.Constants.Companion.EXTRA_ROOM_DATA
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import toast

class LoadingMatchActivity : AppCompatActivity() {

    // Test Room ID
    val dummyRoomID = "dV9Ph9wMyEBmjdrHMbrs"

    // View Model
    private val loadingMatchViewModel: LoadingMatchViewModel by lazy {
        ViewModelProvider(
            this,
            LoadingMatchVMFactory(LoadingMatchImpl(MatchmakingRepoImpl()))
        ).get(LoadingMatchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_match)

        fetchRoomData()

    }

    private fun fetchRoomData() {
        loadingMatchViewModel.getRoomData(dummyRoomID)
        loadingMatchViewModel.roomData.observe(this, Observer {task ->
            when (task) {
                is Resource.Loading -> {
                    // Add Loading Screen
                }

                is Resource.Success -> {
                    toast("Success")
                    Log.d("loadingMatchDebug: ", task.data!!.roomID)
                    Log.d("loadingMatchDebug: ", task.data.questions[0].toString())
                    intentToCamera(task.data)
                }

                is Resource.Failure -> {
                    toast(task.throwable.message.toString())
                }

                else -> {
                    // do nothing
                    toast(task.toString())
                }
            }
        })
    }

    private fun intentToCamera(roomModel: RoomModel) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra(EXTRA_ROOM_DATA, roomModel)
        startActivity(intent)
        finish()
    }
}