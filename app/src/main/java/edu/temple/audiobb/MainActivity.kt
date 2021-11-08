package edu.temple.audiobb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface {

    var twoPane = false
    lateinit var bookViewModel: BookViewModel
    var bookList: BookList = BookList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //launch search activity
        val searchButton = findViewById<Button>(R.id.mainSearchButton)
        searchButton.setOnClickListener {
            val intent = Intent(this, BookSearchActivity::class.java).apply {
                putExtra("booklist", bookList)
            }
            startActivity(intent)
        }

        twoPane = findViewById<View>(R.id.container2) != null
        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        // Pop DisplayFragment from stack if book was previously selected,
        // but user has since cleared selection
        if(supportFragmentManager.findFragmentById(R.id.container1)
                    is BookDetailsFragment
            && bookViewModel.getBook().value?.title.isNullOrBlank())
                supportFragmentManager.popBackStack()

        // Remove redundant DisplayFragment if we're moving from single-pane mode
        // (one container) to double pane mode (two containers)
        // and a color has been selected
        if(supportFragmentManager.findFragmentById(R.id.container1) is BookDetailsFragment
            && twoPane)
                supportFragmentManager.popBackStack()


        //val bookTitles = resources.getStringArray(R.array.Book_Titles)
        //val bookAuthors = resources.getStringArray(R.array.Book_Authors)

        //initialize booklist
        /*for(i in 0..10){
            val b: Book = Book(bookTitles[i], bookAuthors[i], 0, "lol")
            booklist.add(b)
        }*/

        // If fragment was not previously loaded (first time starting activity)
        // then add SelectionFragment
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance(bookList))
                .commit()

        // If second container is available, place an
        // instance of DisplayFragment
        if (twoPane) {
            if (supportFragmentManager.findFragmentById(R.id.container2) == null)
                supportFragmentManager.beginTransaction()
                    .add(R.id.container2, BookDetailsFragment())
                    .commit()
        } else if(!bookViewModel.getBook().value?.title.isNullOrBlank()) { // If moving to single-pane
            supportFragmentManager.beginTransaction()                 // but a book was selected
                .replace(R.id.container1, BookDetailsFragment())              // before the switch
                .addToBackStack(null)
                .commit()
        }

        /*val booklistFragment = BookListFragment.newInstance(booklist)
        val bookdetailsFragment = BookDetailsFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.container1, booklistFragment)
            .add(R.id.container2, bookdetailsFragment)
            .commit()*/

    }

    override fun selectionMade() {
        if(!twoPane)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
    }

}