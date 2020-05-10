package com.example.snapventuremultiplayer.repository.datasource.remote.mlkit.imagelabeling

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.snapventuremultiplayer.utils.helper.InternetCheck
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ImageLabelingRepoImpl : IImageLabelingRepo {

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun detectFromBitmap(
        context: Context,
        bitmap: Bitmap
    ): Resource<List<FirebaseVisionImageLabel>?> {

        val networkHelper = InternetCheck.getInstance(context)
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        var result: List<FirebaseVisionImageLabel>? = null

        return try {
            Log.d("ImageLabelingRepo: ", "First Init")
            if (networkHelper.isNetworkConnected()) {
                Log.d("ImageLabelingRepo: ", "Network Confirmed")
//            val detectOnline = FirebaseVision.getInstance().cloudImageLabeler
                val detectOnline = FirebaseVision.getInstance().onDeviceImageLabeler
                detectOnline.processImage(image).addOnSuccessListener { firebaseVisionImageLabels ->
                    for (result in firebaseVisionImageLabels) {
                        // Check Image
                        Log.d("ImageLabelingRepo: ", "Result: ${result.text}, Confidence: ${result.confidence}")
                    }
                    result = firebaseVisionImageLabels
                }
            } else {
                val detect = FirebaseVision.getInstance().onDeviceImageLabeler
                detect.processImage(image).addOnSuccessListener { firebaseVisionImageLabels ->
                    for (result in firebaseVisionImageLabels) {
//                    Toast.makeText(Camera.this, "Result : " + result.getText(), Toast.LENGTH_SHORT)
//                        .show();
                    }
                    result = firebaseVisionImageLabels
                }.addOnFailureListener { e -> Log.i("Picture", "Vision Failed : $e") }
            }.await()
            
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }


}