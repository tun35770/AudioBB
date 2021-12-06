package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IsPlayingViewModel: ViewModel() {
    private val isPlaying: MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

    fun setIsPlaying(value: Boolean){
        isPlaying.value = value
    }

    fun getIsPlaying(): LiveData<Boolean> {
        return isPlaying
    }

}