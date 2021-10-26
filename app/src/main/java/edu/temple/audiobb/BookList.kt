package edu.temple.audiobb

class BookList {

    private lateinit var books: Array<Book>
    private var size: Int = 0

    //add a book to the list
    fun add(book: Book): Unit {
        books[size] = book
        size++
    }

    //remove a book from the list
    fun remove(_book: Book): Unit {
        for (i in books.indices) {  //iterate through books

            if (books[i].title == _book.title && books[i].author == _book.author) {
                for(j in i..size){  //shift books left
                    books[i] = books[j]
                }
                size--  //decrement size
            }
        }

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