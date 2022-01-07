package hu.ait.bookexchange.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.bookexchange.BookListActivity
import hu.ait.bookexchange.ClaimedBookActivity
import hu.ait.bookexchange.R
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.ActivityBookListBinding
import hu.ait.bookexchange.databinding.BookListCardBinding
import java.lang.Exception
import java.sql.Time
import java.util.*

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    lateinit var context: Context
    lateinit var currUserId: String
    var bookList = mutableListOf<Book>()
    var bookKeys = mutableListOf<String>()

    val CLAIM_PERIOD = 3 // number of days for claim

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currUserId = uid
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookListCardBinding.inflate(
            (context as BookListActivity).layoutInflater,
            parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[holder.adapterPosition]
        holder.bind(book)

        if (book.imgUrl.isNotEmpty()) {
            Glide.with(context as BookListActivity).load(book.imgUrl).into(holder.binding.ivBook)
        }

        holder.binding.btnClaim.setOnClickListener {
            if (!bookList[holder.adapterPosition].isClaimed) {
                claimBook(holder.adapterPosition, currUserId)
            }
        }
    }

    private fun claimBook(index: Int, currUserId: String) {
        // update book in database

        try {
            bookList[index].isClaimed = true
            bookList[index].claimedBy = currUserId

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, CLAIM_PERIOD)
            bookList[index].claimEndDate = calendar.time

            notifyItemChanged(index)
            val bookCollection =
                FirebaseFirestore.getInstance().collection(BookListActivity.COLLECTION_BOOKS)
            bookCollection.document(bookKeys[index]).set(bookList[index])

            Toast.makeText(
                context as BookListActivity, (context as BookListActivity)
                    .getString(R.string.claim_end_date, bookList[index].claimEndDate.toString()),
                Toast.LENGTH_LONG
            ).show()
        }

        catch (e:Exception){
            // remove claim
            ClaimedBookAdapter(this.context, this.currUserId).unclaimBook(index)

            Log.d("TAG_CLAIM", e.message.toString())

            Toast.makeText(
                context as BookListActivity, context.getString(R.string.claim_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun removeBook(index: Int) {
        FirebaseFirestore.getInstance().collection(BookListActivity.COLLECTION_BOOKS).document(
            bookKeys[index]
        ).delete()

        bookList.removeAt(index)
        bookKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removeBookByKey(key: String) {
        val index = bookKeys.indexOf(key)
        if (index != -1) {
            bookList.removeAt(index)
            bookKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addBook(book: Book, key: String) {
        bookList.add(book)
        bookKeys.add(key)
        notifyItemInserted(bookList.lastIndex)
    }

    fun updateBook(book: Book, key: String) {
        var index = bookKeys.indexOf(key)
        bookList[index] = book
        notifyItemChanged(index)

        val bookCollection =
            FirebaseFirestore.getInstance().collection(BookListActivity.COLLECTION_BOOKS)
        bookCollection.document(bookKeys[index]).set(bookList[index])
    }

    inner class ViewHolder(var binding: BookListCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author
            binding.tvPrice.text =
                (context as BookListActivity).getString(R.string.price, book.price)
            binding.tvCondition.text = (context as BookListActivity).resources
                .getStringArray(R.array.condition_array)[book.condition]
        }
    }
}