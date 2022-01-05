package hu.ait.bookexchange.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.bookexchange.BookListActivity
import hu.ait.bookexchange.ClaimedBookActivity
import hu.ait.bookexchange.R
import hu.ait.bookexchange.RegisterActivity.Companion.COLLECTION_USERS
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.data.User
import hu.ait.bookexchange.databinding.ClaimedBookCardBinding
import java.util.*

class ClaimedBookAdapter: RecyclerView.Adapter<ClaimedBookAdapter.ViewHolder>{
    var context: Context
    private var currUserId: String
    private var  bookList = mutableListOf<Book>()
    private var  bookKeys = mutableListOf<String>()
    lateinit var user : User

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currUserId = uid
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ClaimedBookCardBinding.inflate((context as ClaimedBookActivity).layoutInflater,
            parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[holder.adapterPosition]

        // check claim end date
        if(book.claimEndDate?.after(Calendar.getInstance().time) == true){
            unclaimBook(position)
        }

        holder.bind(book)

        if (book.imgUrl.isNotEmpty()) {
            Glide.with(context as ClaimedBookActivity).load(book.imgUrl).into(holder.binding.ivBook)
        }

        holder.binding.btnDel.setOnClickListener {
            unclaimBook(holder.adapterPosition)
        }

        holder.binding.tvUser.setOnClickListener{
            (context as ClaimedBookActivity).showUserDialog(user)
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

    fun addBook(book: Book, key:String){
        bookList.add(book)
        bookKeys.add(key)
        notifyItemInserted(bookList.lastIndex)
    }

    fun unclaimBook(pos: Int){
        bookList[pos].isClaimed = false
        bookList[pos].claimedBy = ""
        bookList[pos].claimEndDate = null
        notifyItemChanged(pos)
        val bookCollection = FirebaseFirestore.getInstance().collection(BookListActivity.COLLECTION_BOOKS)
        bookCollection.document(bookKeys[pos]).set(bookList[pos])
    }

    fun updateBook(book:Book, key:String){
        var index = bookKeys.indexOf(key)
        bookList[index] = book
        notifyItemChanged(index)
    }

    inner class ViewHolder(var binding: ClaimedBookCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(book: Book){
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author
            binding.tvPrice.text =  (context as ClaimedBookActivity).getString(R.string.price, book.price)
            binding.tvCondition.text = (context as ClaimedBookActivity).resources
                .getStringArray(R.array.condition_array)[book.condition]
            binding.tvClaimEndDate.text = (context as ClaimedBookActivity).getString(R.string.claim_end_date, book.claimEndDate.toString())

            var userCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            userCollection.whereEqualTo("user_id", book.user_id).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if(book.user_id == document.data.get("user_id").toString()){
                            user  = User(
                                book.user_id,
                                document.data.get("name").toString(),
                                document.data.get("email").toString(),
                                document.data.get("phone").toString().toInt(),
                                document.data.get("school").toString()
                            )

                            binding.tvUser.text = (context as ClaimedBookActivity).getString(R.string.owned_by, user.name)

                            break
                        }
                    }
                }
        }
    }
}