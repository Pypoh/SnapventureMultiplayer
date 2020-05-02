package com.example.snapventuremultiplayer.repository.datasource.remote.auth.register

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

class RegisterRepoImpl : IRegisterRepo {
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

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


}