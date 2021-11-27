package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.temple.audlibplayer.PlayerService

class BookProgressViewModel : ViewModel() {

    private val bookProgress: MutableLiveData<PlayerService.BookProgress> by lazy{
        MutableLiveData<PlayerService.BookProgress>()
    }

    private val i: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    fun setBookProgress(newBookProgress: PlayerService.BookProgress){
        this.bookProgress.value = newBookProgress
    }

    fun getBookProgress(): LiveData<PlayerService.BookProgress> {
        return bookProgress
    }

    fun setI(newI: Int){
        this.i.value = newI
    }

    fun getI():LiveData<Int>{
        return i
    }
}