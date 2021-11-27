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
    private lateinit var bookProgress: PlayerService.BookProgress

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

        return layout;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton.setOnClickListener {
            (requireActivity() as ControlInterface).onPlayPressed()
        }

        pauseButton.setOnClickListener {


            if((requireActivity() as ControlInterface).isPlaying()) {
                pauseButton.setText("Resume")
                bookProgress = (requireActivity() as ControlInterface).getProgress()
                seekBar.setProgress(bookProgress.progress)
                Log.d("PROGRESS", bookProgress.progress.toString())
            }
            else
                pauseButton.setText("Pause")

            (requireActivity() as ControlInterface).onPausePressed()
        }

        stopButton.setOnClickListener {
            (requireActivity() as ControlInterface).onStopPressed()
            pauseButton.setText("Pause")
        }



        //seekBar.progress = bookProgress.progress

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

    interface ControlInterface{
        fun onPlayPressed()
        fun onPausePressed()
        fun onStopPressed()
        fun getProgress(): PlayerService.BookProgress
        fun isPlaying(): Boolean
    }
}