package com.example.snapventuremultiplayer.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.snapventuremultiplayer.ui.auth.register.domain.IRegister
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class RegisterViewModel(private val useCase: IRegister) : ViewModel() {

    // Variables
    var email: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    var nim: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var passwordConf: MutableLiveData<String> = MutableLiveData()

    lateinit var result: LiveData<Resource<AuthResult?>>

    fun registerWithEmailAndPassword() {
        result = liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                // Handle Register
                val registerAuthResult: Resource<AuthResult?> =
                    useCase.registerWithEmailAndPassword(
                        email = email.value!!,
                        password = password.value!!
                    )
                emit(registerAuthResult)
            } catch (e: Exception) {
                emit(Resource.Failure(e.cause!!))
            }
        }
    }
}