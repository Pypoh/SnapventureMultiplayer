package com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking

import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.utils.viewobject.Resource

interface IMatchmakingRepo {

    suspend fun getRoomData(roomID: String) : Resource<RoomModel?>

    suspend fun postScore(roomID: String, playerNumber: Int, score: Int)
}