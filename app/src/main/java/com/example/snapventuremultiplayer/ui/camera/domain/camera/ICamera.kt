package com.example.snapventuremultiplayer.ui.camera.domain.camera

import android.content.Context
import android.graphics.Bitmap
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel

interface ICamera {

    suspend fun detectFromBitmap(context: Context, bitmap: Bitmap) : Resource<List<FirebaseVisionImageLabel>?>

}