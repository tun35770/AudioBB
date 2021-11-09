package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso


/**
 * A simple [Fragment] subclass.
 * Use the [BookDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookDetailsFragment : Fragment() {

    lateinit private var bookViewModel: BookViewModel
    lateinit private var titleTextView: TextView
    lateinit private var authorTextView: TextView
    lateinit private var imageView: ImageView
    lateinit private var layout: View

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
        layout = inflater.inflate(R.layout.fragment_book_details, container, false)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)

        titleTextView = layout.findViewById(R.id.detailsTitleTextView)
        authorTextView = layout.findViewById(R.id.detailsAuthorTextView)
        imageView = layout.findViewById(R.id.detailsImageView)

        bookViewModel.getBook().observe(viewLifecycleOwner, Observer{it ->
            titleTextView.text = it.title
            authorTextView.text = it.author
            Picasso.get()
                .load(it.coverURL)
                .into(imageView)
        })
    }

}