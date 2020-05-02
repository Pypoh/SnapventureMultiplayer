package com.example.snapventuremultiplayer.repository.datasource.remote.auth.login

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult

interface ILoginRepo {

    suspend fun loginWithEmailAndPassword(email: String, password: String): Resource<AuthResult?>
}
