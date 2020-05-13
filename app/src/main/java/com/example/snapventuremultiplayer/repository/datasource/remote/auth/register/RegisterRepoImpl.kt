package com.example.snapventuremultiplayer.repository.datasource.remote.auth.register

import android.util.Log
import android.widget.Toast
import com.example.snapventuremultiplayer.repository.model.User
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.components.Lazy
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class RegisterRepoImpl : IRegisterRepo {
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mRef: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override suspend fun registerWithEmailAndPassword(
            email: String,
            password: String
    ): Resource<AuthResult?> {
        return try {
            val data = mAuth
                    .createUserWithEmailAndPassword(email, password)
                    .await()

            Resource.Success(data)
        } catch (e: FirebaseAuthException) {
            Resource.Failure(e)
        }
    }

    override fun insertUserData(name: String, email: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef: DocumentReference = mRef.collection("users").document(userId.toString())
        val userModel = User.UserRegister(name, email)
        return try {
            val  data = userRef.set(userModel).addOnSuccessListener{

            }
        } catch (e: FirebaseFirestoreException) {

        }
    }







}