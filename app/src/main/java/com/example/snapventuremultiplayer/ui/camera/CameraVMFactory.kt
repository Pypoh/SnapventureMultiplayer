package com.example.snapventuremultiplayer.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.ui.camera.domain.camera.ICamera
import com.example.snapventuremultiplayer.ui.camera.domain.score.IScore
import java.lang.RuntimeException

class CameraVMFactory (private val cameraUseCase: ICamera, private val scoreUseCase: IScore) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(ICamera::class.java, IScore::class.java).newInstance(cameraUseCase, scoreUseCase)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}