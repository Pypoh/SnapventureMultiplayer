package com.example.snapventuremultiplayer.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.ui.camera.domain.camera.ICamera
import com.example.snapventuremultiplayer.ui.camera.domain.room.IRoom
import java.lang.RuntimeException

class CameraVMFactory (private val cameraUseCase: ICamera, private val roomUseCase: IRoom) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(ICamera::class.java, IRoom::class.java).newInstance(cameraUseCase, roomUseCase)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}