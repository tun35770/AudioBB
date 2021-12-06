package edu.temple.audiobb

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.service.controls.Control
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import edu.temple.audlibplayer.PlayerService
import java.io.File
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface, ControlFragment.ControlInterface {

    var isConnected = false
    lateinit var mediaControlBinder : PlayerService.MediaControlBinder

    var twoPane = false
    lateinit var bookViewModel: BookViewModel
    lateinit var bookListViewModel: BookListViewModel
    lateinit var bookProgressViewModel: BookProgressViewModel
    lateinit var book: Book
    var bookList: BookList = BookList()

    lateinit var bookProgress: PlayerService.BookProgress
    val progressHandler = Handler(Looper.getMainLooper()) {
        if(it.obj != null) {
            bookProgress = it.obj as PlayerService.BookProgress   //BookProgress object
        }
        true
    }

    val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnected = true
            mediaControlBinder = service as PlayerService.MediaControlBinder
            mediaControlBinder.setProgressHandler(progressHandler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(Intent(this, PlayerService::class.java)
        , serviceConnection
        , BIND_AUTO_CREATE)

        //launch search activity
        val searchButton = findViewById<Button>(R.id.mainSearchButton)
        searchButton.setOnClickListener {
            val intent = Intent(this, BookSearchActivity::class.java).apply {
                putExtra("booklist", bookList)
            }
            startActivityForResult(intent, 1)
        }

        twoPane = findViewById<View>(R.id.container2) != null
        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        bookListViewModel = ViewModelProvider(this).get(BookListViewModel::class.java)  //updating booklist to match user search results
        bookProgressViewModel = ViewModelProvider(this).get(BookProgressViewModel::class.java)

        //get the selected book
        bookViewModel.getBook().observe(this, Observer { it ->
            book = it
        })

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

        // If fragment was not previously loaded (first time starting activity)
        // then add SelectionFragment
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance())
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

        //create ControlFragment if it does not yet exist
        if(savedInstanceState == null)
        supportFragmentManager.beginTransaction()
            .add(R.id.ControlContainer, ControlFragment())
            .commit()
    }


    override fun selectionMade() {
        if(!twoPane)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun onResume() {
        super.onResume()

        //when user searches on BookDetailsFragment
        //BookListFragment will be shown after search
        supportFragmentManager.beginTransaction()
            .replace(R.id.container1, BookListFragment.newInstance())
            .commit()
    }

    //when search activity returns
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val newBookList = data.getParcelableExtra<BookList>("list")!!   //get bookList
                    bookListViewModel.setBookList(newBookList)  //set bookList to result from user search
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::bookProgress.isInitialized)
            bookProgressViewModel.setBookProgress(bookProgress)
        //unbindService(serviceConnection)
    }


    /* ControlFragment Interface functions */

    override fun onPlayPressed() {
        val fileName = "book${book.id}.mp3"
        val file = File(filesDir, fileName);

        //if a book is selected
        if (!bookViewModel.getBook().value?.title.isNullOrBlank()){
            if(file.exists()) { //if book has been downloaded
                mediaControlBinder.play(    File(this.filesDir,    fileName), 0)
                Log.d("FILE", "Book ${book.id} played from file")
            }

            else {  //book has not been downloaded, so stream it
                mediaControlBinder.play(book.id)    //play book
            }
        }



    }

    override fun onPausePressed() {
        mediaControlBinder.pause()  //pause book
    }

    override fun onStopPressed() {
        mediaControlBinder.stop()   //stop book
    }

    override fun getProgress(): PlayerService.BookProgress{
            return bookProgress
    }

    override fun isPlaying(): Boolean{
        return mediaControlBinder.isPlaying
    }

    override fun jumpTo(position: Int){
        mediaControlBinder.seekTo(position)
    }

    override fun isServiceConnected(): Boolean {
        return isConnected
    }

    override fun mediaControlBinderInitialized(): Boolean {
        return this::mediaControlBinder.isInitialized
    }
}