package com.example.snapventuremultiplayer.repository.datasource.remote.auth.other

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class AuthRepoImpl : IAuthRepo {
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var mUser: FirebaseUser

    override suspend fun getAuthInstance(): Resource<FirebaseAuth> {
        return try {
            Resource.Success(mAuth)
        } catch (e: FirebaseAuthException) {
            Resource.Failure(e)
        }
    }

    override suspend fun getUserID(): Resource<FirebaseUser> {
        return try {
            mUser = mAuth.currentUser!!

            Resource.Success(mUser)
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Failure(e)
        }
    }
}
