package com.example.snapventuremultiplayer.ui.auth.login.domain

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult

interface ILogin {

    suspend fun loginWithEmailAndPassword(email: String, password: String): Resource<AuthResult?>
}
