package com.example.snapventuremultiplayer.ui.auth.register.domain

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult

interface IRegister {

    suspend fun registerWithEmailAndPassword(email: String, password: String): Resource<AuthResult?>
    fun insertUserData(name: String, email: String)
}