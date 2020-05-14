package com.example.snapventuremultiplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class ScoreActivity : AppCompatActivity() {

    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var textwin1 : TextView
    private lateinit var textwin2 : TextView
    private var scoreplayer1 : String? = ""
    private var scoreplayer2 : String? = ""
    //INTENT BUNDLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        var extras = intent.extras
        var roomId = extras!!.getString("roomname")

        textwin1 = findViewById(R.id.resultwin1)
        textwin2 = findViewById(R.id.resultwin2)
        textwin1.text = readDataWin1(roomId!!)
        textwin2.text = readDataWin2(roomId!!)


    }

    private fun readDataWin1(roomId: String): String {
        roomId?.let {
            mRef.collection("multiplayer").document(it).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    //baca data yang ada di firestore masukin variabel
                    scoreplayer1 = doc.get("scorePlayer1").toString()
                } else {
                    Log.d("Dashboard checkRoom", "Room Not Found")
                }
            }
        }
        return scoreplayer1!!
    }

    private fun readDataWin2(roomId: String): String {
        roomId?.let {
            mRef.collection("multiplayer").document(it).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    //baca data yang ada di firestore masukin variabel
                    scoreplayer2 = doc.get("scorePlayer2").toString()
                } else {
                    Log.d("Dashboard checkRoom", "Room Not Found")
                }
            }
        }
        return scoreplayer2!!
    }
}