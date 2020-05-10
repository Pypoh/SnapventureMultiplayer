package com.example.snapventuremultiplayer.ui.camera.domain.room

import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.utils.viewobject.Resource

class RoomImpl(private val matchmakingRepoImpl: MatchmakingRepoImpl) : IRoom {
//    override suspend fun getRoomData(roomID: String): Resource<RoomModel?> = matchmakingRepoImpl.getRoomData(roomID)
//
//    override suspend fun postScore(roomID: String, playerNumber: Int, score: Int) = matchmakingRepoImpl.postScore(roomID, playerNumber, score)

}