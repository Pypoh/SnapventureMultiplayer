package com.example.snapventuremultiplayer.repository.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuestionsModel(
    @SerializedName("questionID")
    var questionID: String = "",
    @SerializedName("question")
    var question: String = "",
    @SerializedName("answer")
    var answer: String = ""
) : Serializable {
    constructor() : this("")
}