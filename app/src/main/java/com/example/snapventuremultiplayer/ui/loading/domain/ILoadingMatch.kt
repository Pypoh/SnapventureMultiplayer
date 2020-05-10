package com.example.snapventuremultiplayer.ui.loading.domain

import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.utils.viewobject.Resource

interface ILoadingMatch {

    suspend fun getRoomData(roomID: String) : Resource<RoomModel?>

    suspend fun postScore(roomID: String, playerNumber: Int, score: Int)

}