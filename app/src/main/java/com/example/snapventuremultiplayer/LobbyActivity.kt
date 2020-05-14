package com.example.snapventuremultiplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        var name = ""
        var roomId = ""
        var status = ""
        var uid = ""
        val intent: Intent = getIntent()
        status = intent.getStringExtra("STATUS")
        roomId = intent.getStringExtra("ROOMID")

        Log.d("STATUS", status)

        tv_lobbyId.text = "ROOM ID : " + roomId
        uid = mAuth.currentUser?.uid!!

        val userRef: DocumentReference = mRef.collection("users").document(uid)
        userRef.get().addOnSuccessListener { task ->
            name = task.get("name").toString()
            checkPlayer(status, roomId, name)
        }


        btn_ready.setOnClickListener {
            if (status != null) {
                updateReadyStatus(roomId, status)
            }
        }
    }

    private fun checkPlayer(status: String, roomId: String, name: String) {
        if (status == "1") {
            tv_player1.text = name
            Log.d("debugNama", name)
            checkPlayerAvailable(roomId)
        } else if (status == "2") {
            tv_player2.text = name
            btn_ready.isEnabled = true
            val hostRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
            hostRef.get().addOnSuccessListener { result ->
                val hostId = result.get("userIdPlayer1").toString()
                setContentPlayer1(hostId)
            }
        }
    }

    private fun intentToMatchBattle(status: String, roomId: String) {
        val intent = Intent(this, LoadingMatchActivity::class.java)
        intent.putExtra("STATUS", status)
        intent.putExtra("ROOMID", roomId)
        startActivity(intent)
    }

    private fun checkPlayerAvailable(roomId: String) {
        val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (snapshot.get("userIdPlayer2")!! != "") {
                    val player2Id: String = snapshot.get("userIdPlayer2").toString()
                    btn_ready.isEnabled = true
                    setContentPlayer2(player2Id)
                }

            }
        }
    }

    private fun setContentPlayer2(player2Id: String) {
        val docRef: DocumentReference = mRef.collection("users").document(player2Id)
        docRef.get().addOnSuccessListener { result ->
            val name: String = result.get("name").toString()
            tv_player2.text = name
        }
    }

    private fun setContentPlayer1(player1Id: String) {
        Log.d("PLAYER1ID", player1Id)
        val userRef: DocumentReference = mRef.collection("users").document(player1Id)
        userRef.get().addOnSuccessListener { result ->
            val name: String = result.get("name").toString()
            tv_player1.text = name
        }
    }

    private fun checkReadyPlayer(roomId: String, status: String) {
        btn_ready.isEnabled = false

        val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                if (status == "1") {
                    if (snapshot.get("player2Ready")!! != "false") {
                        player2_status.text = "Ready"
                        intentToMatchBattle(status, roomId)
                    } else {
                        checkReadyPlayer(roomId, status)
                    }
                } else {
                    if (snapshot.get("player1Ready")!! != "false") {
                        player1_status.text = "Ready"
                        intentToMatchBattle(status, roomId)
                    } else {
                        checkReadyPlayer(roomId, status)
                    }
                }
            }
        }
    }

    private fun updateReadyStatus(roomId: String, status: String) {
        val userRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
        if (status == "1") {
            userRef.update("player1Ready", true).addOnSuccessListener { result ->
            }
            checkReadyPlayer(roomId, status)
        } else {
            userRef.update("player2Ready", true).addOnSuccessListener { result ->
            }
            checkReadyPlayer(roomId, status)
        }
    }

}