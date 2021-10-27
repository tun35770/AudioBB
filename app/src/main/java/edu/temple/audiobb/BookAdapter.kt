package edu.temple.audiobb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter (val _context : Context, val _bookList : BookList, val ocl: View.OnClickListener): RecyclerView.Adapter<BookAdapter.ViewHolder>(){

    val context = _context
    val bookList = _bookList

    //ViewHolder class
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val booklistViewRecycler : ImageView

        init {
            // Define click listener for the ViewHolder's View.
            booklistViewRecycler = view.findViewById(R.id.recyclerLayout)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BookAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.booklist_recycler_element, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: BookAdapter.ViewHolder, _position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val position = _position
        val titleTextView = viewHolder.booklistViewRecycler.findViewById<View>(R.id.titleTextView)
        val authorTextView = viewHolder.booklistViewRecycler.findViewById<View>(R.id.authorTextView)

        viewHolder.booklistViewRecycler.setOnClickListener(ocl)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int{ return bookList.size() }

}