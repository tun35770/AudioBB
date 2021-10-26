package edu.temple.audiobb

class BookList {

    private lateinit var books: ArrayList<Book>
    private var size: Int = 0

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

    //get size
    fun size(): Int{
        return size
    }
}