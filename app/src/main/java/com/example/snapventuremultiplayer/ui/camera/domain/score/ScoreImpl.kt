package com.example.snapventuremultiplayer.ui.camera.domain.score

import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl

class ScoreImpl(private val matchmakingRepoImpl: MatchmakingRepoImpl) : IScore {

        override suspend fun postScore(roomID: String, playerNumber: Int, score: Int) = matchmakingRepoImpl.postScore(roomID, playerNumber, score)

}