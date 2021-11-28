package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookPlayingViewModel: ViewModel() {
    private val currentBook: MutableLiveData<Book> by lazy {
        MutableLiveData<Book>()
    }

    fun setCurrentBook(book: Book){
        this.currentBook.value = book
    }

    fun getBook(): LiveData<Book>{
        return currentBook
    }
}