package com.example.snapventuremultiplayer.ui.camera

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.databinding.ActivityCameraBinding
import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl
import com.example.snapventuremultiplayer.repository.datasource.remote.mlkit.imagelabeling.ImageLabelingRepoImpl
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.camera.domain.camera.CameraImpl
import com.example.snapventuremultiplayer.ui.camera.domain.room.RoomImpl
import com.example.snapventuremultiplayer.utils.Constants.Companion.CAMERA_REQUEST_CODE_PERMISSIONS
import com.example.snapventuremultiplayer.utils.Constants.Companion.EXTRA_ROOM_DATA
import com.example.snapventuremultiplayer.utils.Constants.Companion.REQUIRED_PERMISSIONS
import com.example.snapventuremultiplayer.utils.Constants.Companion.RIDDLE_POPUP_HEADER
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.common.util.concurrent.ListenableFuture
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check Permission
        permissionRequest()

        // Get Intent Extras
        getIntentExtras()

        // Init Views
        initViews()

        //Init Game
        initGameActivity()
    }

    private fun initGameActivity() {
        // Set Stage game in viewModel
        cameraViewModel.setStage(0)

        // Setup Riddle Header
        cameraViewModel.currentStage.observe(this, Observer { stage ->
            val header = "$RIDDLE_POPUP_HEADER ${stage + 1}/${cameraViewModel.getStageSize()}"
            binding.popupRiddleCamera.questionHeader.text = header
        })

        // Load Riddle Text
        cameraViewModel.currentRiddle.observe(this, Observer { riddleText ->
            binding.popupRiddleCamera.riddlesTextPopup.setText(riddleText)
        })
    }

    private fun getIntentExtras() {
        val roomData = intent.getSerializableExtra(EXTRA_ROOM_DATA) as RoomModel
        cameraViewModel.setupRoomData(roomData)
    }

    private fun initViews() {
        // Camera
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

        // SnapButton
        binding.snapBtn.setOnClickListener { button ->
            if (button.isEnabled) {
                button.isEnabled = false
                cameraPreview.takePicture()
            } else {
                toast("Analyzing... Please wait.")
            }
        }

        // Riddle Popup Text
        binding.popupRiddleCamera.riddlesTextPopup.setFactory {
            val riddleTextView = TextView(binding.popupRiddleCamera.root.context)
            riddleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            riddleTextView.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            )
            riddleTextView.textSize = 16f
            riddleTextView
        }

        // Riddle Popup Pass Button
        binding.popupRiddleCamera.textPassButton.setOnClickListener {
            cameraViewModel.currentStage.value?.plus(1)
                ?.let { stageNumber -> cameraViewModel.setStage(stageNumber) }
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
                    binding.snapBtn.isEnabled = true
                }

                is Resource.Failure -> {
                    toast(task.throwable.message.toString())
                    binding.snapBtn.isEnabled = true
                }

                else -> {
                    // do nothing
                    toast(task.toString())
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionRequest() {
        if (allPermissionsGranted()) {
//            cameraPreview.post { startCamera() }
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                CAMERA_REQUEST_CODE_PERMISSIONS
            )
        }
    }

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
}