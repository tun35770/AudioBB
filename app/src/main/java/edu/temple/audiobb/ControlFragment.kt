package edu.temple.audiobb

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import edu.temple.audlibplayer.PlayerService

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

    lateinit var book: Book
    lateinit var bookViewModel: BookViewModel
    lateinit var bookProgressViewModel: BookProgressViewModel

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
            seekBar.max = (book.duration)
            playButton.setOnClickListener {
                (requireActivity() as ControlInterface).onPlayPressed()
                textView.text = "Now Playing: ${book.title}"
                running = true
                if(!t.isAlive)
                    t.start()
            }

            pauseButton.setOnClickListener {

                if((requireActivity() as ControlInterface).isPlaying()) {
                    running = false //stop thread
                    pauseButton.setText("Resume")
                }
                else {
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

        bookProgressViewModel = ViewModelProvider(requireActivity()).get(BookProgressViewModel::class.java)
        bookProgressViewModel.getBookProgress().observe(viewLifecycleOwner, Observer{it ->
            bookProgress = it
        })


    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ControlFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ControlFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    val t = Thread(Runnable{
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

        if(this::book.isInitialized)  {
            textView.text = "Now Playing: ${book.title}"
            if(!t.isAlive)  //restart thread after configuration change
                t.start()
        }
    }

}