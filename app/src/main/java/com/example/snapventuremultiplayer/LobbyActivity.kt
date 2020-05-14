package com.example.snapventuremultiplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.snapventuremultiplayer.ui.loading.LoadingMatchActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_lobby.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LobbyActivity : AppCompatActivity() {

    lateinit var status: String
    lateinit var roomId: String
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        val bundle = intent.extras
        status = bundle?.getString("STATUS")!!
        roomId = bundle?.getString("ROOMID")!!

        status?.let {
            GlobalScope.launch {
                checkPlayer(it, roomId)
            }
        }
        btn_ready.setOnClickListener {
            if (status != null) {
                GlobalScope.launch {
                    updateReadyStatus(roomId, status)
                }
            }
        }
    }

    private suspend fun checkPlayer(status: String, roomId: String) {
        val uid = mAuth.currentUser?.uid
        var name: String = ""
        val userRef: DocumentReference = mRef.collection("users").document(uid.toString())
        userRef.get().addOnSuccessListener { task ->
            name = task.get("name").toString()
        }.await()
        if (status == "1") {
            tv_player1.text = name
            GlobalScope.launch {
                checkPlayerAvailable(roomId)
            }
        } else {
            tv_player2.text = name
            btn_ready.isEnabled = true
            var hostId = ""
            val hostRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
            hostRef.get().addOnSuccessListener { result ->
                hostId = result.get("userIdPlayer1").toString()
            }.await()
            setContentPlayer(hostId, status)
        }
    }

    private fun intentToMatchBattle() {
        startActivity(Intent(this, LoadingMatchActivity::class.java))
    }

    private suspend fun checkPlayerAvailable(roomId: String) {
        val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (snapshot.get("userIdPlayer2")!! != "") {
                    val player2Id: String = snapshot.get("userIdPlayer2").toString()
                    btn_ready.isEnabled = true
                    GlobalScope.launch {
                        setContentPlayer(player2Id, status)
                    }
                }

            }
        }
    }

    private suspend fun setContentPlayer(playerId: String, status: String) {
        if (status == "1") {
            val docRef: DocumentReference = mRef.collection("users").document(playerId)
            docRef.get().addOnSuccessListener { result ->
                val name: String = result.get("name").toString()
                tv_player2.text = name
            }.await()
        } else {
            val userRef: DocumentReference = mRef.collection("users").document(playerId)
            userRef.get().addOnSuccessListener { result ->
                val name: String = result.get("name").toString()
                tv_player1.text = name
            }
        }
    }

    private fun checkReadyPlayer(roomId: String, status: String) {
        btn_ready.isEnabled = false

        val docRef: DocumentReference = mRef.document(roomId)
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (status == "1") {
                    if (snapshot.get("player2Ready")!! != "false") {
                        player2_status.text = "Ready"
                        intentToMatchBattle()
                    } else {
                        checkReadyPlayer(roomId, status)
                    }
                } else {
                    if (snapshot.get("player1Ready")!! != "false") {
                        player1_status.text = "Ready"
                        intentToMatchBattle()
                    } else {
                        checkReadyPlayer(roomId, status)
                    }
                }
            }
        }
    }

    private suspend fun updateReadyStatus(roomId: String, status: String) {

    }

}