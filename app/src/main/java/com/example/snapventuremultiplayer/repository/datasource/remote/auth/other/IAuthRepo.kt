package com.example.snapventuremultiplayer.repository.datasource.remote.auth.other

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface IAuthRepo {

    suspend fun getAuthInstance(): Resource<FirebaseAuth>

    suspend fun getUserID(): Resource<FirebaseUser>
}
