package com.example.snapventuremultiplayer.ui.dashboard

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.repository.model.QuestionsModel
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.home.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    var roomIds: MutableList<String> = ArrayList()
    var questionIds: MutableList<String> = ArrayList()
    val questionDataSet: ArrayList<QuestionsModel> = ArrayList()

    // New Code
    var questionList: ArrayList<QuestionsModel> = ArrayList()

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationButton: ImageButton
    private lateinit var mode1vs1Button: ImageButton
    private lateinit var mode2vs2Button: ImageButton
    private lateinit var mode3vs3Button: ImageButton
    private lateinit var createRoomButton: ImageButton
    private lateinit var joinRoomButton: ImageButton
    private lateinit var freeRoomButton: ImageButton
    private lateinit var quickStartButton: ImageButton
    private lateinit var PlayerName: TextView
    private lateinit var LevelPlayer: TextView
    private lateinit var TextLokasi: TextView
    private lateinit var Textmodee: TextView

    private fun setupViewBinding(view: View) {
        locationButton = view.findViewById(R.id.lokasiMain)
        mode1vs1Button = view.findViewById(R.id.btn1vs1)
        mode2vs2Button = view.findViewById(R.id.btn2vs2)
        mode3vs3Button = view.findViewById(R.id.btn3vs3)
        createRoomButton = view.findViewById(R.id.btncreateRoom)
        joinRoomButton = view.findViewById(R.id.btnjoinRoom)
        freeRoomButton = view.findViewById(R.id.btnfreeRoom)
        quickStartButton = view.findViewById(R.id.btnQuickStart)
        PlayerName = view.findViewById(R.id.namaUser)
        LevelPlayer = view.findViewById(R.id.levelUser)
        TextLokasi = view.findViewById(R.id.lokasi)
        Textmodee = view.findViewById(R.id.modeMain)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setupViewBinding(root)
        return root
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mode1vs1Button.setOnClickListener {
            mode1vs1Button.isEnabled = false
            GlobalScope.launch {
                getAllQuestions()
                Log.d("DashboardFragment", "Getting all question")
            }
        }
    }
    private suspend fun getAllQuestions() {
        try {
            val colRef: CollectionReference = mRef.collection("questions")
            questionIds.clear()
            colRef.get().addOnCompleteListener { task ->
                for (document: QueryDocumentSnapshot in task.result!!) {
                    questionIds.add(document.id)
                }
                questionIds.shuffle()
                val questionLength = 3

                val questionTemp: MutableList<String> = questionIds.subList(0, questionLength)
                questionIds = questionTemp
            }.await()

            getQuestionData()

        } catch (e: FirebaseFirestoreException) {
            Log.d("DashboardFragment: ", e.message + "")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun getQuestionData() {
        try {
            questionDataSet.clear()
            for (questionId: String in questionIds) {
                val questionRef: DocumentReference =
                        mRef.collection("questions").document(questionId)
                questionRef.get().addOnSuccessListener { documents ->
                    val questionModel: QuestionsModel =
                            documents.toObject(QuestionsModel::class.java)!!
                    questionDataSet.add(questionModel)
                }.await()
            }
        } catch (e: FirebaseFirestoreException) {

        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun getAllRoomId() {
        try {
            val colRef: CollectionReference = mRef.collection("multiplayer")
            colRef.get().addOnCompleteListener { task ->
                for (document: QueryDocumentSnapshot in task.result!!) {
                    roomIds.add(document.id)
                }
            }.await()

//            generateRoomId()

            checkRoomId(randomKey())

        } catch (e: FirebaseFirestoreException) {

        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun checkRoomId(roomId: String) {
        mRef.collection("multiplayer").document(roomId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                GlobalScope.launch {
                    checkRoomId(randomKey())
                }
            } else {
                GlobalScope.launch {
                    createRoomDatabase(roomId)
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun generateRoomId() {
        try {
            val roomId = randomKey()
            for (room_id: String in roomIds) {
                if (room_id != roomId) {

                }
            }
            createRoomDatabase(roomId)
        } catch (e: FirebaseFirestoreException) {
            generateRoomId()
        }
    }

    suspend fun createRoomDatabase(roomId: String) {

            generateRoomId()


    }

    private fun intentToLobby() {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun randomKey(): String {
        var rand_num: Int = Random.nextInt(100000, 999999)
//        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//        java.util.Random().ints(2, 0, source.length)
//                .toArray()
//                .map(source::get)
//                .joinToString("")
        return rand_num.toString()
    }


}