package com.example.snapventuremultiplayer.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.ui.auth.register.domain.IRegister

class RegisterVMFactory(private val useCase: IRegister) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IRegister::class.java).newInstance(useCase)
    }
}
