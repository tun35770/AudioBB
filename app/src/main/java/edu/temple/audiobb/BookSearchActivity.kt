package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.lang.System.out

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

    //fetches books from api
    private fun fetchBookData(searchTerm: String){
        val url = "https://kamorris.com/lab/cis3515/search.php?term=${searchTerm}"
        val file = File(this.filesDir, "list.txt")

        //delete existing list
        if(file.exists())
            file.delete()

        if(getIntent().extras != null)
            bookList = intent.getParcelableExtra<BookList>("booklist")!!

        volleyQueue.add(
            JsonArrayRequest(Request.Method.GET
            , url
            , null
            , {
               //Log.d("Response", it.toString())
                    try{
                        bookList.setEmpty()    //empty the current booklist
                        val resultIntent: Intent = Intent()

                        for(i in 0 until it.length()){  //get response of books
                            val responseObject = it.getJSONObject(i)
                            val b = Book(responseObject.getString("title")
                            , responseObject.getString("author")
                            , responseObject.getInt("id")
                            , responseObject.getString("cover_url")
                            , responseObject.getInt("duration"))

                            bookList.add(b)    //add book to bookList

                            //write book info to list file
                            /*file.bufferedWriter().use{
                                out.println(b.title)
                                out.println(b.author)
                                out.println(b.id)
                                out.println(b.coverURL)
                                out.println(b.duration)
                            }*/

                           FileWriter(file, true).use{
                                it.append("${b.title}\n".toString())
                                it.append("${b.author}\n".toString())
                                it.append("${b.id}\n".toString())
                                it.append("${b.coverURL}\n".toString())
                                it.append("${b.duration}\n".toString())

                            }

                        }

                        resultIntent.putExtra("list", bookList)
                        setResult(Activity.RESULT_OK, resultIntent)
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