package com.example.snapventuremultiplayer.repository.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoomModel (
    @SerializedName("userIdPlayer1")
    var userIdPlayer1: String = "",
    @SerializedName("userIdPlayer2")
    var userIdPlayer2: String = "",
    @SerializedName("scorePlayer1")
    var scorePlayer1: Long = 0,
    @SerializedName("scorePlayer2")
    var scorePlayer2: Long = 0,
    @SerializedName("winner")
    var winner: String = "",
    @SerializedName("player1Ready")
    var player1Ready: Boolean = false,
    @SerializedName("SerializedName")
    var player2Ready: Boolean = false,
    @SerializedName("roomID")
    var roomID: String = "",
    @SerializedName("questions")
    var questions: ArrayList<QuestionsModel> = ArrayList()
) : Serializable {
    constructor() : this("",
        "", 0,
        0, "",
        false, false,
        "", ArrayList()
    )
}