package edu.temple.audiobb

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.Control
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import edu.temple.audlibplayer.PlayerService
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream

/**
 * A simple [Fragment] subclass.
 * Use the [ControlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ControlFragment : Fragment() {

    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var textView: TextView
    private lateinit var bookProgress: PlayerService.BookProgress
    private lateinit var playingBook: Book

    val volleyQueue : RequestQueue by lazy{
        Volley.newRequestQueue(requireContext())
    }

    lateinit var book: Book
    lateinit var bookViewModel: BookViewModel
    lateinit var bookProgressViewModel: BookProgressViewModel
    lateinit var bookPlayingViewModel: BookPlayingViewModel

    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var layout = inflater.inflate(R.layout.fragment_control, container, false)
        playButton = layout.findViewById(R.id.ControlFragment_PlayButton)
        pauseButton = layout.findViewById(R.id.ControlFragment_PauseButton)
        stopButton = layout.findViewById(R.id.ControlFragment_StopButton)
        seekBar = layout.findViewById<SeekBar>(R.id.ControlFragment_SeekBar)
        textView = layout.findViewById(R.id.ControlFragment_TextView)

        return layout;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
        bookViewModel.getBook().observe(viewLifecycleOwner, Observer{it ->
            book = it

            playButton.setOnClickListener {
                (requireActivity() as ControlInterface).onPlayPressed()

                val fileName = "book${book.id}.mp3"
                val file = File(requireContext().filesDir, fileName);

                //if not already downloaded
                if(!file.exists())
                    downloadBook(book.id)

                textView.text = "Now Playing: ${book.title}"
                running = true
                if(!t.isAlive)
                    t.start()

                seekBar.max = (book.duration)   //set seekbar's length
                bookPlayingViewModel.setCurrentBook(book)
            }

            pauseButton.setOnClickListener {

                if((requireActivity() as ControlInterface).isPlaying()) {   //if currently playing
                    running = false //stop thread
                    pauseButton.setText("Resume")
                }
                else {  //if currently paused
                    running = true  //start thread
                    pauseButton.setText("Pause")
                }

                (requireActivity() as ControlInterface).onPausePressed()
            }

            stopButton.setOnClickListener {
                (requireActivity() as ControlInterface).onStopPressed()
                textView.text = ""
                running = false
                pauseButton.setText("Pause")
            }

            seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(book != null && fromUser)
                        (requireActivity() as ControlInterface).jumpTo((progress))
                    Log.d("Progress:::", progress.toString())
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    Log.d("New Progress", (seekBar.progress.toString()))

                }
            })
        })

        //get bookprogress object
        bookProgressViewModel = ViewModelProvider(requireActivity()).get(BookProgressViewModel::class.java)
        bookProgressViewModel.getBookProgress().observe(viewLifecycleOwner, Observer{it ->
            bookProgress = it
        })

        //get the current playing book
        bookPlayingViewModel = ViewModelProvider(requireActivity()).get(BookPlayingViewModel::class.java)
        bookPlayingViewModel.getBook().observe(viewLifecycleOwner, Observer{it ->
            playingBook = it
        })

    }

    val t = Thread(Runnable{    //Thread for seekbar updates
        Thread.sleep(3000)
        while(running){
            bookProgress = (requireActivity() as ControlInterface).getProgress()
            seekBar.setProgress(bookProgress.progress)
            Log.d("Progress", bookProgress.progress.toString())
            Thread.sleep(1000)

            while(!running);
        }
    })

    interface ControlInterface{
        fun onPlayPressed() //play button
        fun onPausePressed()    //pause button
        fun onStopPressed() //stop button
        fun getProgress(): PlayerService.BookProgress   //BookProgress object
        fun isPlaying(): Boolean    //check if audio is playing
        fun jumpTo(position: Int)   //seekTo
        fun isServiceConnected(): Boolean   //check if service is connected
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
    }

    override fun onResume() {
        super.onResume()
        running = true

        if(this::playingBook.isInitialized && !t.isAlive)  //restart thread after configuration change
                t.start()

        if(this::playingBook.isInitialized) {   //reinitialize some values after configuration change
            textView.text = "Now Playing: ${playingBook.title}"
            seekBar.max = (playingBook.duration)
        }
    }

    fun downloadBook(id: Int){
        val url = "https://kamorris.com/lab/audlib/download.php?id=${id}"

        val newRequest = InputStreamVolleyRequest(
            Request.Method.GET
        , url
        , {
                try{
                    val outputStream: FileOutputStream
                    val name = "book${id}.mp3"
                    outputStream = requireContext().openFileOutput(name, Context.MODE_PRIVATE)
                    outputStream.write(it)
                    Toast.makeText(requireContext(), "Successfully downloaded book ${book.id}", Toast.LENGTH_SHORT).show()
                } catch(e : Exception){
                    e.printStackTrace()
                }
            }
        , {
                Toast.makeText(requireContext(), "Failed to download book!", Toast.LENGTH_SHORT).show()
            })

        volleyQueue.add(newRequest)

    }
}