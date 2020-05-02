package com.example.snapventuremultiplayer.ui.auth.login.domain

import com.example.snapventuremultiplayer.repository.datasource.remote.auth.login.ILoginRepo
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult

class LoginImpl(private val loginRepository: ILoginRepo) : ILogin {

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Resource<AuthResult?> = loginRepository.loginWithEmailAndPassword(email, password)
}
