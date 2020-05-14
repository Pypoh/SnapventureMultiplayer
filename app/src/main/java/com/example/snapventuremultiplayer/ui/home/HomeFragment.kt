package com.example.snapventuremultiplayer.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.LobbyActivity
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.repository.model.QuestionsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.Result.Companion.failure
import kotlin.collections.ArrayList
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    var roomIds: MutableList<String> = ArrayList()
    var questionIds: MutableList<String> = ArrayList()
    val questionDataSet: ArrayList<QuestionsModel> = ArrayList()

    // New Code
    var questionList: ArrayList<QuestionsModel> = ArrayList()

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

    private lateinit var player_status:String

    // New Code Atifa
    private lateinit var codeEditText: EditText
    private var roomName = ""

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
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        setupViewBinding(root)
        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createRoomButton.setOnClickListener {
            createRoomButton.isEnabled = false
            GlobalScope.launch {
                getAllQuestions()
                Log.d("DashboardFragment", "Getting all question")
            }
        }

        //button join klik keluar alert dialog
        joinRoomButton.setOnClickListener {
            //Edit text di alert dialog
            codeEditText = EditText(context)
            codeEditText.setPadding(
                resources.getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0,
                resources.getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0
            )
            codeEditText.hint = "Room Code"
            val alert = AlertDialog.Builder(context)
                .setTitle("Join Room")
                .setView(codeEditText)
                .setMessage("Please enter the room Code")
                //set positif button
                .setPositiveButton("Join") { dialog, _ ->
                    //simpen data yang dimasukkin di edittext ke var roomname
                    roomName = codeEditText.text.toString()
                    Log.d("DashboardFragment: ", "$roomName joining...")
                    codeEditText.setText("") //set text jd kosongan
                    if (roomName != "") { //kalo ngga kosong
                        GlobalScope.launch {
                            addEventListener(roomName)
                        }
                        Log.d("DashboardFragment: ", "$roomName adding event")
                    }
                    dialog.cancel()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.create()
            alert.show()
        }
    }

    private suspend fun addEventListener(roomId: String) {
        val userId = mAuth.currentUser?.uid
        //read from database firestore
//        mRef.collection("multiplayer")
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
//                        val kode = result.documents
//                        if (roomName.equals(kode)) {
//                            preferences = requireContext().getSharedPreferences("PREFS", 0)
//                            val editor: SharedPreferences.Editor = preferences.edit()
//                            editor.putString("roomname", roomName)
//                            editor.apply()
//                            //pindah fragment
//                        } else {
//                            Toast.makeText(context, "Sorry can't get any Data Error!!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Toast.makeText(context, "Sorry can't get any Data Error!!", Toast.LENGTH_SHORT).show()
//                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
//                }

        mRef.collection("multiplayer").document(roomId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                Log.d("Dashboard checkRoom", "Room Found")
                player_status = "2"
            } else {
                Log.d("Dashboard checkRoom", "Room Not Found")
            }
        }.await()
        val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
        docRef.update("userIdPlayer2", userId).addOnSuccessListener { task ->

        }.await()
        intentToLobby(player_status, roomId)
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

            getAllRoomId()

        } catch (e: FirebaseFirestoreException) {
            Log.d("DashboardFragment: ", e.message + "")
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

    private suspend fun createRoomDatabase(roomId: String) {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val roomModel = hashMapOf(
                "userIdPlayer1" to userId,
                "userIdPlayer2" to "",
                "scorePlayer1" to -1,
                "scorePlayer2" to -1,
                "player1Ready" to false,
                "player2Ready" to false,
                "roomId" to roomId
            )

            val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
            docRef.set(roomModel).addOnSuccessListener { result ->
                Log.d("DashboardFragment: ", "$result")
            }.await()
            for ((index, questionId: String) in questionIds.withIndex()) {
                val docCollection: DocumentReference =
                    docRef.collection("questions").document(questionId)
                docCollection.set(questionDataSet[index]).addOnCompleteListener { result ->

                }.await()

            }
            val player_status = "1"
            intentToLobby(player_status, roomId)

        } catch (e: FirebaseFirestoreException) {

        }
    }

    private fun intentToLobby(status: String, roomId: String) {
        Log.d("DashboardFragment", "Success")
        val intent = Intent(context, LobbyActivity::class.java)
        intent.putExtra("STATUS", status)
        intent.putExtra("ROOMID", roomId)
        startActivity(intent)
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