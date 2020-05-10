package com.example.snapventuremultiplayer.ui.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapventuremultiplayer.ui.loading.domain.ILoadingMatch

class LoadingMatchVMFactory (private val useCase: ILoadingMatch) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ILoadingMatch::class.java).newInstance(useCase)
    }
}