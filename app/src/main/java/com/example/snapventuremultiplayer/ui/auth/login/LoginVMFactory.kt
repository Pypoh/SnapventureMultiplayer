package com.example.snapventuremultiplayer.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.ui.auth.login.domain.ILogin

class LoginVMFactory(private val useCase: ILogin) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ILogin::class.java).newInstance(useCase)
    }
}
