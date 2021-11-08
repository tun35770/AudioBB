package edu.temple.audiobb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException

class BookSearchActivity : AppCompatActivity() {

    val editText : EditText by lazy{
        findViewById<EditText>(R.id.searchEditTextView)
    }

    val searchButton : Button by lazy{
        findViewById<Button>(R.id.searchButtonView)
    }

    val volleyQueue : RequestQueue by lazy{
        Volley.newRequestQueue(this)
    }

    lateinit var bookList: BookList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        searchButton.setOnClickListener{
            fetchBookData(editText.text.toString())
        }

    }

    private fun fetchBookData(searchTerm: String){
        val url = "https://kamorris.com/lab/cis3515/search.php?term=${searchTerm}"

        if(getIntent().extras != null)
            bookList = intent.getParcelableExtra<BookList>("booklist")!!

        volleyQueue.add(
            JsonArrayRequest(Request.Method.GET
            , url
            , null
            , {
                Log.d("Response", it.toString())
                    try{
                        bookList?.setEmpty()    //empty the current booklist

                        for(i in 0 until it.length()){  //get response
                            val responseObject = it.getJSONObject(i)
                            val b = Book(responseObject.getString("title")
                            , responseObject.getString("author")
                            , responseObject.getInt("id")
                            , responseObject.getString("cover_url"))

                            bookList?.add(b)    //add book to bookList
                        }
                        finish()    //back to main activity

                    } catch(e : JSONException){
                        e.printStackTrace()
                        finish()    //back to main activity
                    }
                }
            , {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }

            )
        )
    }
}