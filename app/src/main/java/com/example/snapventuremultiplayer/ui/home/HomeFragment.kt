package com.example.snapventuremultiplayer.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.repository.model.QuestionsModel
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.loading.LoadingMatchActivity
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.android.material.button.MaterialButton
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

    lateinit var matchTestButton: MaterialButton
//    lateinit var btn_vs1: ImageButton
//    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
//
//    var roomIds: MutableList<String> = ArrayList()
//    var questionIds: MutableList<String> = ArrayList()
//    val questionDataSet: ArrayList<QuestionsModel> = ArrayList()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        matchTestButton = root.findViewById(R.id.match_test_btn)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        btn_vs1.setOnClickListener {
//            GlobalScope.launch {
//                getAllQuestions()
//                getAllRoomId()
//            }
//        }
        matchTestButton.setOnClickListener {
            intentToLoadingMatch()
        }
    }

    private fun intentToLoadingMatch() {
        startActivity(Intent(context, LoadingMatchActivity::class.java))
    }


//    suspend fun getAllQuestions() {
//        return try {
//            val colRef: CollectionReference = mRef.collection("questions")
//            questionIds.clear()
//            colRef.get().addOnCompleteListener { task ->
//                for (document: QueryDocumentSnapshot in task.getResult()!!) {
//                    questionIds.add(document.id)
//                }
//                Collections.shuffle(questionIds)
//                var questionLength = 3
//
//                var questionTemp: MutableList<String> = questionIds.subList(0, questionLength)
//                questionIds = questionTemp
//            }.await()
//            getQuestionData()
//        } catch (e: FirebaseFirestoreException) {
//        }
//    }

//    suspend fun getQuestionData() {
//        return try {
//            questionDataSet.clear()
//            for (questionId: String in questionIds) {
//                val questionRef: DocumentReference =
//                        mRef.collection("questions").document(questionId)
//                questionRef.get().addOnSuccessListener { documents ->
//                    val questionModel: QuestionsModel =
//                            documents.toObject(QuestionsModel::class.java)!!
//                    questionDataSet.add(questionModel)
//                }.await()
//            }
//        } catch (e: FirebaseFirestoreException) {
//
//        }
//
//    }

//    suspend fun getAllRoomId() {
//        return try {
//            val colRef: CollectionReference = mRef.collection("multiplayer")
//            colRef.get().addOnCompleteListener { task ->
//                for (document: QueryDocumentSnapshot in task.getResult()!!) {
//                    roomIds.add(document.id)
//                }
//            }.await()
//            generateRoomId()
//        } catch (e: FirebaseFirestoreException) {
//
//        }
//    }

//    suspend fun generateRoomId() {
//        return try {
//            var roomId = randomKey()
//            for (room_id: String in roomIds) {
//                if (!room_id.equals(roomId)) {
//                }
//            }
//            createRoomDatabase(roomId)
//        } catch (e: FirebaseFirestoreException) {
//            generateRoomId()
//        }
//    }

//    suspend fun createRoomDatabase(roomId: String) {
//        return try {
//            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
//            val roomModel = RoomModel(userId, "", -1, -1, "", false, false, roomId, questionDataSet)
//            val docRef: DocumentReference = mRef.collection("multiplayer").document(roomId)
//            docRef.set(roomModel).addOnSuccessListener { result ->
//            }.await()
//            intentToLobby()
//        } catch (e: FirebaseFirestoreException) {
//
//        }
//    }

//    private fun intentToLobby() {
//
//    }

//    private fun randomKey(): String {
//        var rand_num: Int = Random.nextInt(10000, 100000)
//        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//        java.util.Random().ints(2, 0, source.length)
//                .toArray()
//                .map(source::get)
//                .joinToString("")
//        val roomId = rand_num.toString() + source
//        return roomId
//    }
}