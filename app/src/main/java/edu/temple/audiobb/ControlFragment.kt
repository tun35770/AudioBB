package edu.temple.audiobb

import android.content.Context
import android.os.Bundle
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import edu.temple.audlibplayer.PlayerService
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
    lateinit var isPlayingViewModel: IsPlayingViewModel

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

        //preferences for saving book progresses
        var sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        isPlayingViewModel = ViewModelProvider(requireActivity()).get(IsPlayingViewModel::class.java)
        isPlayingViewModel.getIsPlaying().observe(viewLifecycleOwner, Observer{it ->
            if(it)
                pauseButton.text = "Pause"
            else
                pauseButton.text = "Resume"
        })

        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
        bookViewModel.getBook().observe(viewLifecycleOwner, Observer{it ->
            book = it

            playButton.setOnClickListener {

                //save current books progress
                if((requireActivity() as ControlInterface).isPlaying()){
                    sharedPref?.edit()?.putInt("BookPosition${playingBook.id}", bookProgress.progress)?.commit()
                }

                //play book

                val savedPosition = sharedPref?.getInt("BookPosition${book.id}", 0)
                Log.d("POS", savedPosition.toString())
                (requireActivity() as ControlInterface).onPlayPressed(savedPosition)

                //set playingBook viewmodel
                bookPlayingViewModel.setCurrentBook(book)

                val fileName = "book${book.id}.mp3"
                val file = File(requireContext().filesDir, fileName);

                //if not already downloaded
                if(!file.exists())
                    downloadBook(book.id)

                textView.text = "Now Playing: ${playingBook.title}"
                running = true

                isPlayingViewModel.setIsPlaying((true))
                seekBar.max = (book.duration)   //set seekbar's length
            }

            pauseButton.setOnClickListener {

                if((requireActivity() as ControlInterface).isPlaying()) {   //if currently playing
                    running = false //stop thread
                    isPlayingViewModel.setIsPlaying(false)
                    //save progress
                    sharedPref?.edit()?.putInt("BookPosition${playingBook.id}", bookProgress.progress)?.commit()
                }
                else {  //if currently paused
                    running = true  //start thread
                    isPlayingViewModel.setIsPlaying(true)
                }

                (requireActivity() as ControlInterface).onPausePressed()
            }

            stopButton.setOnClickListener {
                (requireActivity() as ControlInterface).onStopPressed()
                textView.text = ""
                running = false
                isPlayingViewModel.setIsPlaying(false)
                sharedPref?.edit()?.putInt("BookPosition${playingBook.id}", 0)?.commit()

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

            //start thread if it isnt already running
            if(t.state == Thread.State.NEW)
                t.start()
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
            if((requireActivity() as ControlInterface).bookProgressInitialized()) {
                bookProgress = (requireActivity() as ControlInterface).getProgress()
                seekBar.setProgress(bookProgress.progress)
                Log.d("Progress", bookProgress.progress.toString())

            }

            Thread.sleep(1000)

            while(!running);
        }
    })

    interface ControlInterface{
        fun onPlayPressed(pos: Int?) //play button
        fun onPausePressed()    //pause button
        fun onStopPressed() //stop button
        fun getProgress(): PlayerService.BookProgress   //BookProgress object
        fun isPlaying(): Boolean    //check if audio is playing
        fun jumpTo(position: Int)   //seekTo
        fun isServiceConnected(): Boolean   //check if service is connected
        fun bookProgressInitialized(): Boolean  //check if bookProgress has been initialized
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
    }

    override fun onResume() {
        super.onResume()
        running = true

        if(this::playingBook.isInitialized) {   //reinitialize some values after configuration change
            textView.text = "Now Playing: ${playingBook.title}"
            seekBar.max = (playingBook.duration)

        }

    }

    //download a book from server
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