package com.example.snapventuremultiplayer.ui.camera

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.ScoreActivity
import com.example.snapventuremultiplayer.databinding.ActivityCameraBinding
import com.example.snapventuremultiplayer.repository.datasource.remote.firestore.matchmaking.MatchmakingRepoImpl
import com.example.snapventuremultiplayer.repository.datasource.remote.mlkit.imagelabeling.ImageLabelingRepoImpl
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.camera.domain.camera.CameraImpl
import com.example.snapventuremultiplayer.ui.camera.domain.score.ScoreImpl
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
import java.util.*

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var resultDialog: Dialog
    private lateinit var passDialog: Dialog
    private lateinit var waitingDialog: Dialog

    private lateinit var textToSpeech: TextToSpeech

    private val cameraViewModel: CameraViewModel by lazy {
        ViewModelProvider(
                this,
                CameraVMFactory(CameraImpl(ImageLabelingRepoImpl()), ScoreImpl(MatchmakingRepoImpl()))
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
        // Setup Lives
        cameraViewModel.lives.value = 5
        cameraViewModel.score.value = 0
        cameraViewModel.opponentFinish.value = true

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

        // Setup Progress Bar
        // TODO: Smooth Horizontal Progress Bar

        // Listen to opponent
        listenToOpponentState()
    }

    private fun listenToOpponentState() {
        cameraViewModel.opponentFinish?.observe(this, Observer { state ->
            if (!state) {
                createWaitingDialog()
            } else {
                if (waitingDialog.isShowing) {
                    waitingDialog.dismiss()
                }
            }
        })

        cameraViewModel.scoreIntent.observe(this, Observer { state ->
            if (state) {
                intentToScore()
            }
        })
    }

    private fun getIntentExtras() {
        val roomData = intent.getSerializableExtra(EXTRA_ROOM_DATA) as RoomModel
        cameraViewModel.setupRoomData(roomData)
    }

    private fun initViews() {
        // Dialog
        waitingDialog = Dialog(this)

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
            passValidator()
        }

        binding.cameraQuestionButton.setOnClickListener {
            changeQuestionState()
        }
    }

    private fun changeQuestionState() {
        val questionLayout = findViewById<ConstraintLayout>(R.id.popup_riddle_camera)

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        if (questionLayout.visibility == View.INVISIBLE) {
            questionLayout.visibility = View.VISIBLE
            questionLayout.startAnimation(slideUp)
            Log.d("animationDebug", "SlideUp")
        } else {
            questionLayout.visibility = View.INVISIBLE
            questionLayout.startAnimation(slideDown)
            Log.d("animationDebug", "SlideDown")
        }
    }

    private fun passValidator() {
        passDialog = Dialog(this)
        passDialog.setContentView(R.layout.dialog_pass)
        passDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val noButton = passDialog.findViewById<Button>(R.id.button_pass_no)
        noButton.setOnClickListener {
            passDialog.dismiss()
        }

        val yesButton = passDialog.findViewById<Button>(R.id.button_pass_ya)
        yesButton.setOnClickListener {
            passDialog.dismiss()
            cameraViewModel.nextStage()
            cameraViewModel.resetTimer()
        }

        passDialog.show()
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
                    binding.snapBtn.isEnabled = true
                    var answered = false
                    loop@ for (result in task.data!!) {
                        Log.d(
                                "CameraActivity: ",
                                "Result: ${result.text}, Confidence: ${result.confidence}"
                        )

                        Log.d(
                                "CameraActivity: ",
                                "Result: ${result.text}, Expected: ${cameraViewModel.currentAnswer.value}"
                        )

                        // Process Result
                        when (result.text) {
                            cameraViewModel.currentAnswer.value -> {

                                cameraViewModel.resetTimer()
                                cameraViewModel.nextStage()
                                cameraViewModel.increaseScore()
                                answered = true
                                Log.d(
                                        "CameraActivity: ",
                                        "Correct Answer, Result: ${result.text}, Going into next round..."
                                )

                                createResultDialog(result.text, true, null)
                                break@loop
                            }
                        }
                    }

                    if (!answered) {
                        when (cameraViewModel.lives.value) {
                            1 -> {
                                // TODO: Add Result Fail
                                toast("You lose")

                            }
                            else -> {
                                // Show Wrong Dialog here
                                // TODO: Show Wrong Dialog
//                                toast("Wrong answer, current live: ${cameraViewModel.lives.value}")
                                cameraViewModel.decreaseLive(1)
                                createResultDialog(null, false, cameraViewModel.lives.value)
                            }
                        }
                    }
                    // DEBUG
//                    cameraViewModel.nextStage()
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

    private fun createResultDialog(result: String?, correctAnswer: Boolean, remainingLives: Int?) {
        resultDialog = Dialog(this)
        resultDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (correctAnswer) {
            resultDialog.setContentView(R.layout.dialog_result_success)
            resultDialog.setCancelable(false)
            resultDialog.setCanceledOnTouchOutside(false)

            val pronounceTextView1: TextView = resultDialog.findViewById(R.id.pronounce_1)
            val pronounceTextView2: TextView = resultDialog.findViewById(R.id.pronounce_2)
            val resultTextView: TextView = resultDialog.findViewById(R.id.text_result_name)

            val vowel = checkVowel(result!!)
            val resultFormat = "That is $vowel $result"
            resultTextView.text = resultFormat
            pronounceTextView1.text = result
            pronounceTextView2.text = result

            textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.language = Locale.US
                }
            })

            // Button
            val nextButton = resultDialog.findViewById<Button>(R.id.button_result_ok)
            nextButton.setOnClickListener {
                resultDialog.dismiss()
            }

            val speechButton = resultDialog.findViewById<ImageView>(R.id.button_result_voice)
            speechButton.setOnClickListener {
                textToSpeech.speak(result, TextToSpeech.QUEUE_FLUSH, null)
            }
        } else {
            resultDialog.setContentView(R.layout.dialog_result_fail)
            resultDialog.setCancelable(false)
            resultDialog.setCanceledOnTouchOutside(false)

            val textFail = resultDialog.findViewById<TextView>(R.id.text_result_fail)
            val textFailFormat = "Oops, Almost There.\n$remainingLives live remaining"
            textFail.text = textFailFormat
            val nextButton = resultDialog.findViewById<Button>(R.id.button_result_ok)
            nextButton.setOnClickListener {
                resultDialog.dismiss()
                Log.d("resultDialog", "Result Button Pressed")
            }
        }

        resultDialog.setOnShowListener {
//            snapLayout.setVisibility(View.GONE)
            cameraPreview.close()
        }

        resultDialog.setOnDismissListener {
//            snapLayout.setVisibility(View.VISIBLE)
            cameraPreview.open()
        }

        resultDialog.show()
    }

    private fun checkVowel(vowel: String): String? {
        val firstLetter = vowel[0]
        return if (firstLetter == 'a' || firstLetter == 'i' || firstLetter == 'u' || firstLetter == 'e' || firstLetter == 'o') {
            "an"
        } else "a"
    }

    private fun createWaitingDialog() {
        waitingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waitingDialog.setContentView(R.layout.dialog_waiting_opponent_finish)
        waitingDialog.setCancelable(false)
        waitingDialog.setCanceledOnTouchOutside(false)

        // Create animation
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_animation)
        val logoView = waitingDialog.findViewById<ImageView>(R.id.loading_logo)
        logoView.startAnimation(bounceAnimation)

        waitingDialog.show()
    }

    private fun intentToScore() {
        var toScore: Intent = Intent(this@CameraActivity, ScoreActivity::class.java)
        toScore.putExtra("roomname", cameraViewModel.roomData.value!!.roomID)
        startActivity(toScore)
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