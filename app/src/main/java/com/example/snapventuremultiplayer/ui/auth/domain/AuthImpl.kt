package com.example.snapventuremultiplayer.ui.auth.domain

import com.example.snapventuremultiplayer.repository.datasource.remote.auth.other.IAuthRepo
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.FirebaseAuth

class AuthImpl(private val authRepository: IAuthRepo) : IAuth {

    override suspend fun getAuthInstance(): Resource<FirebaseAuth> = authRepository.getAuthInstance()
}
