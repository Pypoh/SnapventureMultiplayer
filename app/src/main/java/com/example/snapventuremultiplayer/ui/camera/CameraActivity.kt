package com.example.snapventuremultiplayer.ui.camera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.databinding.ActivityCameraBinding
import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl
import com.example.snapventuremultiplayer.repository.datasource.remote.mlkit.imagelabeling.ImageLabelingRepoImpl
import com.example.snapventuremultiplayer.ui.camera.domain.camera.CameraImpl
import com.example.snapventuremultiplayer.ui.camera.domain.room.RoomImpl
import com.example.snapventuremultiplayer.ui.loading.LoadingMatchVMFactory
import com.example.snapventuremultiplayer.ui.loading.LoadingMatchViewModel
import com.example.snapventuremultiplayer.ui.loading.domain.LoadingMatchImpl
import com.example.snapventuremultiplayer.utils.Constants.Companion.CAMERA_REQUEST_CODE_PERMISSIONS
import com.example.snapventuremultiplayer.utils.Constants.Companion.REQUIRED_PERMISSIONS
import com.example.snapventuremultiplayer.utils.helper.InternetCheck
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import toast
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private val cameraViewModel: CameraViewModel by lazy {
        ViewModelProvider(
            this,
            CameraVMFactory(CameraImpl(ImageLabelingRepoImpl()), RoomImpl(MatchmakingRepoImpl()))
        ).get(CameraViewModel::class.java)
    }

    private lateinit var cameraPreview: CameraView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imagePreview: Preview
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private lateinit var imageCapture: ImageCapture

    private val executor = Executors.newSingleThreadExecutor()

    private var linearZoom = 0f

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraPreview = binding.cameraView
        cameraPreview.setLifecycleOwner(this)
        cameraPreview.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraPreview.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)

        cameraPreview.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bitmap ->
                    analyzeImage(bitmap!!)
                }
            }
        })

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        if (allPermissionsGranted()) {
//            cameraPreview.post { startCamera() }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                CAMERA_REQUEST_CODE_PERMISSIONS
            )
        }

        binding.snapBtn.setOnClickListener {
            cameraPreview.takePicture()
//            binding.snapBtn.isEnabled = false
        }
    }

    private fun analyzeImage(bitmap: Bitmap) {
        cameraViewModel.detectFromBitmap(this, bitmap)
        cameraViewModel.detectionData.observe(this, Observer { task ->
            when (task) {
                is Resource.Loading -> {
                    // Add Loading Screen
                }

                is Resource.Success -> {
                    toast("Success")
                    for (result in task.data!!) {
                        Log.d(
                            "CameraActivity: ",
                            "Result: ${result.text}, Confidence: ${result.confidence}"
                        )
                    }
                }

                is Resource.Failure -> {
                    toast(task.throwable.message.toString())
                }

                else -> {
                    // do nothing
                    toast(task.toString())
                }
            }
        })
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun analyzeImage(bitmap: Bitmap?) {
//        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
//        val networkHelper = InternetCheck.getInstance(this)
//        Log.d(
//            "CameraActivityInit: ",
//            "Working..."
//        )
//        if (networkHelper.isNetworkConnected()) {
//            Log.d(
//                "CameraActivity: ",
//                "Working..."
//            )
////            val detectOnline = FirebaseVision.getInstance().cloudImageLabeler
//            val detectOnline = FirebaseVision.getInstance().onDeviceImageLabeler
//            detectOnline.processImage(image).addOnSuccessListener { firebaseVisionImageLabels ->
//                for (result in firebaseVisionImageLabels) {
//                    // Check Image
//                    Log.d(
//                        "CameraActivity: ",
//                        "Result: ${result.text}, Confidence: ${result.confidence}"
//                    )
//                }
//            }
//        } else {
//            val detect = FirebaseVision.getInstance().onDeviceImageLabeler
//            detect.processImage(image).addOnSuccessListener { firebaseVisionImageLabels ->
//                for (result in firebaseVisionImageLabels) {
//                    //                                Toast.makeText(Camera.this, "Result : " + result.getText(), Toast.LENGTH_SHORT).show();
//                    Log.d(
//                        "CameraActivityNoInt: ",
//                        "Result: ${result.text}, Confidence: ${result.confidence}"
//                    )
//                }
//            }.addOnFailureListener { e -> Log.i("CameraActivityNoInt", "Vision Failed : $e") }
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
//                cameraPreview.post { startCamera() }
            } else {
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun isNetworkConnected(): Boolean {
//        val connectivityManager =
//                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork = connectivityManager.activeNetwork
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
//        return networkCapabilities != null &&
//                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//    }
}