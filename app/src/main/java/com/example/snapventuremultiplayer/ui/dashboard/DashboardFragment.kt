package com.example.snapventuremultiplayer.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mode1vs1Button.setOnClickListener {
            GlobalScope.launch {
                getAllQuestions()
                getAllRoomId()
            }
        }
    }

    suspend fun getAllQuestions() {
        return try {
            val colRef: CollectionReference = mRef.collection("questions")
            questionIds.clear()
            colRef.get().addOnCompleteListener { task ->
                for (document: QueryDocumentSnapshot in task.getResult()!!) {
                    questionIds.add(document.id)
                }
                Collections.shuffle(questionIds)
                var questionLength = 3

                var questionTemp: MutableList<String> = questionIds.subList(0, questionLength)
                questionIds = questionTemp
            }.await()
            getQuestionData()
        } catch (e: FirebaseFirestoreException) {
        }
    }

    suspend fun getQuestionData() {
        return try {
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

    suspend fun getAllRoomId() {
        return try {
            val colRef: CollectionReference = mRef.collection("multiplayer")
            colRef.get().addOnCompleteListener { task ->
                for (document: QueryDocumentSnapshot in task.getResult()!!) {
                    roomIds.add(document.id)
                }
            }.await()
            generateRoomId()
        } catch (e: FirebaseFirestoreException) {

        }
    }

    suspend fun generateRoomId() {
        return try {
            var roomId = randomKey()
            for (room_id: String in roomIds) {
                if (!room_id.equals(roomId)) {
                }
            }
            createRoomDatabase(roomId)
        } catch (e: FirebaseFirestoreException) {
            generateRoomId()
        }
    }

    suspend fun createRoomDatabase(roomId: String) {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val roomModel = RoomModel(userId, "", -1, -1, "", false, false, roomId, questionDataSet)
            val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
            docRef.set(roomModel).addOnSuccessListener { result ->
            }.await()
            intentToLobby()
        } catch (e: FirebaseFirestoreException) {

        }
    }

    private fun intentToLobby() {

    }

    private fun randomKey(): String {
        var rand_num: Int = Random.nextInt(10000, 100000)
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        java.util.Random().ints(2, 0, source.length)
                .toArray()
                .map(source::get)
                .joinToString("")
        val roomId = rand_num.toString() + source
        return roomId
    }



}