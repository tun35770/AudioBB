package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.temple.audlibplayer.PlayerService

class BookProgressViewModel : ViewModel() {

    private val bookProgress: MutableLiveData<PlayerService.BookProgress> by lazy{
        MutableLiveData<PlayerService.BookProgress>()
    }

    fun setBookProgress(newBookProgress: PlayerService.BookProgress){
        this.bookProgress.value = newBookProgress
    }

    fun getBookProgress(): LiveData<PlayerService.BookProgress> {
        return bookProgress
    }
}