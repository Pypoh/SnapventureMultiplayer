package com.example.snapventuremultiplayer.ui.camera.domain.score

interface IScore {

    suspend fun postScore(roomID: String, playerNumber: Int, score: Int)

}