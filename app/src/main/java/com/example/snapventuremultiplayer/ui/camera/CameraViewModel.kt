package com.example.snapventuremultiplayer.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.auth.domain.IAuth
import com.example.snapventuremultiplayer.ui.camera.domain.camera.ICamera
import com.example.snapventuremultiplayer.ui.camera.domain.score.IScore
import com.example.snapventuremultiplayer.utils.Constants
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class CameraViewModel(private val cameraUseCase: ICamera, private val scoreUseCase: IScore) :
    ViewModel() {

    val playerNumber: MutableLiveData<Int> = MutableLiveData()
    val score: MutableLiveData<Int> = MutableLiveData()

    val lives: MutableLiveData<Int> = MutableLiveData()

    // Gameplay
    var currentStage: MutableLiveData<Int> = MutableLiveData()
    var currentRiddle: MutableLiveData<String> = MutableLiveData()
    var currentAnswer: MutableLiveData<String> = MutableLiveData()

    var roomData: MutableLiveData<RoomModel> = MutableLiveData()

    private lateinit var timer: CountDownTimer

    lateinit var detectionData: LiveData<Resource<List<FirebaseVisionImageLabel>?>>

    val userID = "id1"

    fun detectFromBitmap(context: Context, bitmap: Bitmap) {
        detectionData = liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val detectionResult: Resource<List<FirebaseVisionImageLabel>?> =
                    cameraUseCase.detectFromBitmap(context, bitmap)
                emit(detectionResult)
            } catch (e: Exception) {
                emit(Resource.Failure(e.cause!!))
            }
        }
    }

    fun setupRoomData(roomModel: RoomModel) {
        this.roomData.value = roomModel

        if (userID == roomModel.userIdPlayer1) {
            playerNumber.value = 1
        } else {
            playerNumber.value = 2
        }
    }

    fun setStage(stageNumber: Int) {
        try {
            // Setup Temp for Undo
            val tempCurrentStage = currentStage.value

            currentStage.value = stageNumber
            if (currentStage.value!! < getStageSize()) {
                val questionModel = roomData.value?.questions?.get(stageNumber)
                if (questionModel != null) {
                    currentRiddle.value = questionModel.question
                    currentAnswer.value = questionModel.answer
                    Log.d("CameraViewModel: ", "Round $stageNumber initiated")
                }
            } else {
                currentStage.value = tempCurrentStage
            }

        } catch (e: Exception) {
            Log.d("CameraViewModel: ", "Error: ${e.message}")
        }
    }

    fun setTimer() {
        timer = object: CountDownTimer(Constants.RIDDLE_STAGE_TIMEOUT, 1000) {
            override fun onFinish() {
                // When finished, check next riddle
                Log.d("CameraViewModel: ", "Next Round")
            }

            override fun onTick(millisUntilFinished: Long) {
                // Set 5 sec left notification
                Log.d("CameraViewModel: ", "$millisUntilFinished sec")
            }
        }.start()
    }

    fun nextStage() {
        // Check if final stage
        if (currentStage.value == getStageSize()) {
            processFinalResult()
            return
        }

        // Increase Stage Level
        val stage = currentStage.value!!.plus(1)

        setStage(stage)
    }

    private fun processFinalResult() {
        // Send data to database
        val roomID = roomData.value!!.roomID
        val playerNumber = playerNumber.value

        viewModelScope.launch(Dispatchers.IO) {
            if (playerNumber != null) {
                score.value?.let { scoreUseCase.postScore(roomID, playerNumber, it) }
            }
        }
    }

    fun increaseScore() {
        val newScore = score.value!!.plus(1)

        score.value = newScore
    }

    fun decreaseLive(number: Int) {
        val live = lives.value!!.minus(number)

        lives.value = live
    }

    fun getStageSize(): Int {
        return roomData.value?.questions!!.size
    }

}