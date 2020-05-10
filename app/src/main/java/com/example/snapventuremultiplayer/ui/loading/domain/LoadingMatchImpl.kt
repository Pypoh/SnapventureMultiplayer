package com.example.snapventuremultiplayer.ui.loading.domain

import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.utils.viewobject.Resource

class LoadingMatchImpl(private val matchmakingRepoImpl: MatchmakingRepoImpl) : ILoadingMatch {
    override suspend fun getRoomData(roomID: String): Resource<RoomModel?> =
        matchmakingRepoImpl.getRoomData(roomID)

    override suspend fun postScore(roomID: String, playerNumber: Int, score: Int) =
        matchmakingRepoImpl.postScore(roomID, playerNumber, score)

}