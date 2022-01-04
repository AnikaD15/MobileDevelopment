package hu.ait.bookexchange.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.bookexchange.BookListActivity
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.BookItemBinding
import java.sql.Time
import java.util.*

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.ViewHolder>  {
    lateinit var context: Context
    lateinit var currUserId: String
    var  bookList = mutableListOf<Book>()
    var  bookKeys = mutableListOf<String>()

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currUserId = uid
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookItemBinding.inflate((context as BookListActivity).layoutInflater,
            parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[holder.adapterPosition]
        holder.bind(book)

        holder.binding.btnClaim.setOnClickListener {
            if(!bookList[holder.adapterPosition].isClaimed)
                claimBook(holder.adapterPosition, currUserId)
        }
    }

    private fun claimBook(index: Int, currUserId: String) {
        // update book in database

        bookList[index].isClaimed = true
        bookList[index].claimedBy = currUserId

//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DATE, CLAIM_PERIOD)
//        bookList[index].claimEndDate = calendar.time
    }

    // when I remove the post object
    private fun removeBook(index: Int) {
        FirebaseFirestore.getInstance().collection(BookListActivity.COLLECTION_BOOKS).document(
            bookKeys[index]
        ).delete()

        bookList.removeAt(index)
        bookKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    // when somebody else removes an object
    fun removeBookByKey(key: String) {
        val index = bookKeys.indexOf(key)
        if (index != -1) {
            bookList.removeAt(index)
            bookKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addBook(book:Book, key:String){
        bookList.add(book)
        bookKeys.add(key)
        notifyItemInserted(bookList.lastIndex)
    }

    inner class ViewHolder(var binding: BookItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(book: Book){
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author
            binding.tvPrice.text = book.price.toString()

            /*if (book.user_id == currUserId) {
                binding.cardView.visibility = View.GONE
            } else {
                binding.cardView.visibility = View.VISIBLE
            }*/
        }
    }
}