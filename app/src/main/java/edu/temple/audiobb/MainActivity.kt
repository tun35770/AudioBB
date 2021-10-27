package edu.temple.audiobb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val booklist: BookList = BookList()
        val bookTitles = resources.getStringArray(R.array.Book_Titles)
        val bookAuthors = resources.getStringArray(R.array.Book_Authors)

        //initialize booklist
        for(i in 0..10){
            val b: Book = Book(bookTitles[i], bookAuthors[i])
            booklist.add(b)
        }


        val booklistFragment = BookListFragment.newInstance(booklist)
        val bookdetailsFragment = BookDetailsFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.booklistFragmentContainer, booklistFragment)
            .add(R.id.bookdetailsFragmentContainer, bookdetailsFragment)
            .commit()

    }


}