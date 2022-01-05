package hu.ait.bookexchange.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.bookexchange.BookListActivity
import hu.ait.bookexchange.BookListActivity.Companion.COLLECTION_BOOKS
import hu.ait.bookexchange.UserBookListActivity
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.BookItemBinding
import hu.ait.bookexchange.databinding.OwnedBookItemBinding
import java.util.*

class OwnedBookAdapter: RecyclerView.Adapter<OwnedBookAdapter.ViewHolder>{
    var context: Context
    private var currUserId: String
    private var  bookList = mutableListOf<Book>()
    private var  bookKeys = mutableListOf<String>()

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currUserId = uid
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OwnedBookItemBinding.inflate((context as UserBookListActivity).layoutInflater,
            parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[holder.adapterPosition]
        holder.bind(book)

        holder.binding.btnEdit.setOnClickListener {
            (context as UserBookListActivity).showEditBookDialog(book, bookKeys[position])
        }

        holder.binding.btnDel.setOnClickListener {
            removeBook(holder.adapterPosition)
        }
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

    fun removeBookByKey(key: String) {
        val index = bookKeys.indexOf(key)
        if (index != -1) {
            bookList.removeAt(index)
            bookKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addBook(book: Book, key:String){
        bookList.add(book)
        bookKeys.add(key)
        notifyItemInserted(bookList.lastIndex)
    }

    inner class ViewHolder(var binding: OwnedBookItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(book: Book){
            binding.tvTitle.text = book.title
        }
    }
}