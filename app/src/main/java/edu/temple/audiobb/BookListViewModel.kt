package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookListViewModel: ViewModel() {

    private val bookList: MutableLiveData<BookList> by lazy{
        MutableLiveData<BookList>()
    }

    fun setBookList(_bookList: BookList): Unit{
        this.bookList.value = _bookList
    }

    fun getBookList(): LiveData<BookList> {
        return bookList
    }

}