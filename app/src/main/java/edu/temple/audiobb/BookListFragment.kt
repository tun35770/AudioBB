package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView


/**
 * A simple [Fragment] subclass.
 * Use the [BookListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BookListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var booklist: BookList
    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            booklist = it.getParcelable<BookList>("list")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_list, container, false)
        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)

        recyclerView = layout.findViewById(R.id.booklistRecyclerView)//get recyclerView reference

        //create the on click listener
        val ocl: View.OnClickListener = View.OnClickListener { view ->
            val pos : Int = recyclerView.getChildAdapterPosition(view)
            bookViewModel.setBook(booklist.get(pos))
            (activity as EventInterface).selectionMade()
        }

        recyclerView.adapter = BookAdapter(requireContext(), booklist, ocl) // pass booklist and ocl to adapter

        return layout
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookListFragment.
         */

        @JvmStatic
        fun newInstance(booklist: BookList) =
            BookListFragment().apply {
                arguments = Bundle().apply{
                    putParcelable("list", booklist)
            }

            /*:BookListFragment{
            val fragment = BookListFragment()
            fragment.booklist = booklist
            return fragment*/
        }
    }

    interface EventInterface{
        fun selectionMade()
    }
}
