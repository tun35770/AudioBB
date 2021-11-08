package edu.temple.audiobb

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

class BookList() : Parcelable {

    private var books: ArrayList<Book> = arrayListOf()
    private var size: Int = 0

    constructor(parcel: Parcel) : this() {
        size = parcel.readInt()
        parcel.readList(books, Book::class.java.getClassLoader())
    }

    //add a book to the list
    fun add(book: Book): Unit {
        books.add(book)
        size++
    }

    //remove a book from the list
    fun remove(book: Book): Unit {
        books.remove(book)
        size--
    }

    //get book at index
    fun get(index: Int): Book{
        return books[index]
    }

    fun setEmpty(): Unit{
        books = ArrayList<Book>()
        size = 0;
    }

    //get size
    fun size(): Int{
        return size
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(size)
        parcel.writeList(books)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookList> {
        override fun createFromParcel(parcel: Parcel): BookList {
            return BookList(parcel)
        }

        override fun newArray(size: Int): Array<BookList?> {
            return arrayOfNulls(size)
        }
}
}