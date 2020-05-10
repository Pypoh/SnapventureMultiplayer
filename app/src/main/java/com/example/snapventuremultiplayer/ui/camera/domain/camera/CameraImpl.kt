package com.example.snapventuremultiplayer.ui.camera.domain.camera

import android.content.Context
import android.graphics.Bitmap
import com.example.snapventuremultiplayer.repository.datasource.remote.mlkit.imagelabeling.IImageLabelingRepo
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel

class CameraImpl(private val imageLabelingRepo: IImageLabelingRepo) :
    ICamera {
    override suspend fun detectFromBitmap(
        context: Context,
        bitmap: Bitmap
    ): Resource<List<FirebaseVisionImageLabel>?> =
        imageLabelingRepo.detectFromBitmap(context, bitmap)
}
