package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {

    private var book: MutableLiveData<Book>
        get() {
            return book
        }
        set(value) {book = value}


    fun setBook(book: Book): Unit{
        this.book.value = book
    }

    fun getBook(): LiveData<Book> {
        return book
    }
}