package com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking

import android.util.Log
import com.example.snapventuremultiplayer.repository.model.QuestionsModel
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class MatchmakingRepoImpl : IMatchmakingRepo {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val multiplayerReference: CollectionReference by lazy { db.collection("multiplayer") }
    private val questionsReference: CollectionReference by lazy { db.collection("questions") }
    private var roomObject: RoomModel? = null

    override suspend fun getRoomData(roomID: String): Resource<RoomModel?> {
        return try {
            multiplayerReference
                .document(roomID)
                .get()
                .addOnSuccessListener { result ->
                    roomObject = result.toObject(RoomModel::class.java)
                }.await()

            val questionListTemp = ArrayList<QuestionsModel>()

            multiplayerReference
                .document(roomID)
                .collection("questions")
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val questionObject = document.toObject(QuestionsModel::class.java)
                        questionListTemp.add(questionObject)
                    }
                }.await()

            for (questions in questionListTemp) {
                questionsReference
                    .document(questions.questionID)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        roomObject?.questions!!.add(snapshot.toObject(QuestionsModel::class.java)!!)
                    }.await()
            }
            Log.d("loadingMatchRepoResult", roomObject!!.questions.size.toString())
            Resource.Success(roomObject)
        } catch (e: FirebaseFirestoreException) {
            Resource.Failure(e)
        }
    }

    override suspend fun postScore(roomID: String, playerNumber: Int, score: Int) {
        try {
            val player = if (playerNumber == 1) "scorePlayer1" else "scorePlayer2"
            multiplayerReference
                .document(roomID)
                .update(player, score)
                .addOnSuccessListener {
                    // TODO: Add Return
                }

        } catch (e: FirebaseFirestoreException) {
            Log.e("MatchMakingRepoImpl: ", e.message!!)
        }
    }

    override suspend fun listenMatchmaking() {
        TODO("Not yet implemented")
    }

}