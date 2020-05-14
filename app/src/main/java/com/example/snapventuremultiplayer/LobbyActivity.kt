package com.example.snapventuremultiplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LobbyActivity : AppCompatActivity() {

    lateinit var readyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        val bundle = intent.extras
        val status = bundle?.getInt("STATUS")

        status?.let { checkPlayer(it) }
        readyButton.setOnClickListener {
            intentToMatchBattle()
        }
    }

    private fun checkPlayer(status: Int) {
        if (status == 1) {
//            checkPlayerAvailable()
        } else {

        }
    }

    private fun intentToMatchBattle() {
        TODO("Not yet implemented")
    }

    private suspend fun checkPlayerAvailable() {

    }

}