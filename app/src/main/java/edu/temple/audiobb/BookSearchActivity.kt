package edu.temple.audiobb

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

    lateinit var testText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        searchButton.setOnClickListener{
            fetchBookData(editText.text.toString())
        }

    }

    private fun fetchBookData(searchTerm: String){
        val url = "https://kamorris.com/lab/cis3515/search.php?term=${searchTerm}"

        volleyQueue.add(
            JsonArrayRequest(Request.Method.GET
            , url
            , null
            , {
                Log.d("Response", it.toString())
                    try{
                        testText = it.getJSONObject(0).getString("title")
                        Toast.makeText(this, testText, Toast.LENGTH_SHORT).show()
                    } catch(e : JSONException){
                        e.printStackTrace()
                    }
                }
            , {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }

            )
        )
    }
}