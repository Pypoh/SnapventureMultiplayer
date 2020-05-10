package com.example.snapventuremultiplayer.repository.datasource.remote.mlkit.imagelabeling

import android.content.Context
import android.graphics.Bitmap
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel

interface IImageLabelingRepo {

    suspend fun detectFromBitmap(context: Context, bitmap: Bitmap) : Resource<List<FirebaseVisionImageLabel>?>

}