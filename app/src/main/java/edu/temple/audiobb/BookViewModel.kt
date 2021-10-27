package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {

    private val book: MutableLiveData<Book> by lazy {
        MutableLiveData<Book>()
    }



    fun setBook(book: Book): Unit{
        this.book.value = book
    }

    fun getBook(): LiveData<Book> {
        return book
    }
}