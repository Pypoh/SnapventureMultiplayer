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

    lateinit var roomData: RoomModel

    lateinit var detectionData: LiveData<Resource<List<FirebaseVisionImageLabel>?>>

    fun detectFromBitmap(context: Context, bitmap: Bitmap) {
        detectionData = liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val detectionResult: Resource<List<FirebaseVisionImageLabel>?> =
                    cameraUseCase.detectFromBitmap(context, bitmap)
                emit(detectionResult)
            } catch (e: Exception) {
                Log.d("CameraViewModel: ", "Error: ${e.message}")
                Log.d("CameraViewModel: ", "Error: ${e.cause}")
                emit(Resource.Failure(e.cause!!))
            }
        }
    }

}