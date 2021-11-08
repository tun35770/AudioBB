package edu.temple.audiobb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class BookSearchActivity : AppCompatActivity() {

    val editText = findViewById<EditText>(R.id.searchEditTextView)
    val searchButton = findViewById<Button>(R.id.searchButtonView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)



    }
}