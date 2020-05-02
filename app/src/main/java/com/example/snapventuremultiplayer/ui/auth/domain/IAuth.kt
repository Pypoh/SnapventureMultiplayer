package com.example.snapventuremultiplayer.ui.auth.domain

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.FirebaseAuth

interface IAuth {

    suspend fun getAuthInstance(): Resource<FirebaseAuth>
}
