package com.example.snapventuremultiplayer.repository.datasource.remote.auth.register

import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult

interface IRegisterRepo {

    suspend fun registerWithEmailAndPassword(email: String, password: String): Resource<AuthResult?>
}