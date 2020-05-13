package com.example.snapventuremultiplayer.ui.auth.register.domain

import com.example.snapventuremultiplayer.repository.datasource.remote.auth.register.IRegisterRepo
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult

class RegisterImpl(private val registerRepository: IRegisterRepo) : IRegister {

    override suspend fun registerWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<AuthResult?> = registerRepository.registerWithEmailAndPassword(email, password)

    override fun insertUserData(name: String, email: String) {
        registerRepository.insertUserData(name, email)
    }
}