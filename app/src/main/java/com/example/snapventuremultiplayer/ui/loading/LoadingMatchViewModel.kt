package com.example.snapventuremultiplayer.ui.loading

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.snapventuremultiplayer.repository.model.RoomModel
import com.example.snapventuremultiplayer.ui.loading.domain.ILoadingMatch
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class LoadingMatchViewModel(private val loadingMatchUseCase: ILoadingMatch) : ViewModel() {

    val roomID: MutableLiveData<String> = MutableLiveData()

    lateinit var roomData: LiveData<Resource<RoomModel?>>

    fun getRoomData(roomID: String? = this.roomID.value) {
        roomData = liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            try {
                val roomResult: Resource<RoomModel?> = loadingMatchUseCase.getRoomData(roomID!!)
                emit(roomResult)
            } catch (e: Exception) {
                Log.e("loadingMatchViewModel: ", e.message)
                emit(Resource.Failure(e.cause!!))
            }
        }
    }

}