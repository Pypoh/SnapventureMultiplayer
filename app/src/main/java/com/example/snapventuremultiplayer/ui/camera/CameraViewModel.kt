package com.example.snapventuremultiplayer.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.camera.domain.camera.ICamera
import com.example.snapventuremultiplayer.ui.camera.domain.room.IRoom
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class CameraViewModel(private val cameraUseCase: ICamera, private val roomUseCase: IRoom) :
    ViewModel() {

    val roomID: MutableLiveData<String> = MutableLiveData()
    val playerNumber: MutableLiveData<Int> = MutableLiveData()
    val score: MutableLiveData<Int> = MutableLiveData()

    // Gameplay
    var currentStage: MutableLiveData<Int> = MutableLiveData()
    var currentRiddle: MutableLiveData<String> = MutableLiveData()
    var currentAnswer: MutableLiveData<String> = MutableLiveData()

    var roomData: MutableLiveData<RoomModel> = MutableLiveData()

    lateinit var detectionData: LiveData<Resource<List<FirebaseVisionImageLabel>?>>

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
                }
            } else {
                currentStage.value = tempCurrentStage
            }

        } catch (e: Exception) {
            Log.d("CameraViewModel: ", "Error: ${e.message}")
        }
    }

    fun getStageSize(): Int {
        return roomData.value?.questions!!.size
    }

}