package com.example.snapventuremultiplayer.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.snapventuremultiplayer.ui.auth.domain.IAuth
import com.example.snapventuremultiplayer.utils.Constants
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class SplashScreenViewModel(private val useCase: IAuth) : ViewModel() {

    lateinit var authInstance: LiveData<Resource<FirebaseAuth>>

    init {
        checkAuthInstance()
    }

    private fun checkAuthInstance() {
        authInstance = liveData(Dispatchers.IO) {
            try {
                val authResult: Resource<FirebaseAuth> = useCase.getAuthInstance()
                delay(Constants.SPLASH_SCREEN_DELAY)
                emit(authResult)
            } catch (e: FirebaseAuthException) {
                emit(Resource.Failure(e.cause!!))
            }
        }
    }
}
