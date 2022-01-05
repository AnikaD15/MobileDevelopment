package hu.ait.bookexchange.adapter

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.bookexchange.*
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.data.User
import hu.ait.bookexchange.databinding.OwnedBookCardBinding
import java.util.*

class OwnedBookAdapter: RecyclerView.Adapter<OwnedBookAdapter.ViewHolder>{
    var context: Context
    private var currUserId: String
    private var  bookList = mutableListOf<Book>()
    private var  bookKeys = mutableListOf<String>()
    lateinit var claimUser : User

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currUserId = uid
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OwnedBookCardBinding.inflate((context as OwnedBookActivity).layoutInflater,
            parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[holder.adapterPosition]
        holder.bind(book)

        if (book.imgUrl.isNotEmpty()) {
            Glide.with(context as OwnedBookActivity).load(book.imgUrl).into(holder.binding.ivBook)
        }

        holder.binding.tvUser.setOnClickListener{
            (context as ClaimedBookActivity).showUserDialog(claimUser)
        }

        holder.binding.btnEdit.setOnClickListener {
            (context as OwnedBookActivity).showEditBookDialog(book, bookKeys[holder.adapterPosition])
        }

        holder.binding.btnDel.setOnClickListener {
            removeBook(holder.adapterPosition)
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

    fun updateBook(book: Book, key: String) {
        var index = bookKeys.indexOf(key)
        bookList[index] = book
        notifyItemChanged(index)
    }

    inner class ViewHolder(var binding: OwnedBookCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(book: Book){
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author
            binding.tvPrice.text =  (context as OwnedBookActivity).getString(R.string.price, book.price)
            binding.tvCondition.text = (context as OwnedBookActivity).resources
                .getStringArray(R.array.condition_array)[book.condition]

            if(book.isClaimed){
                var userCollection = FirebaseFirestore.getInstance()
                    .collection(RegisterActivity.COLLECTION_USERS)
                userCollection.whereEqualTo("user_id", book.user_id).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if(book.claimedBy == document.data.get("user_id").toString()){
                                claimUser  = User(
                                    book.user_id,
                                    document.data.get("name").toString(),
                                    document.data.get("email").toString(),
                                    document.data.get("phone").toString().toInt(),
                                    document.data.get("school").toString()
                                )

                                binding.tvUser.text = (context as OwnedBookActivity)
                                    .getString(R.string.claimed_by, claimUser.name)

                                Log.d("TAG_CLAIM_USER", claimUser.name)
                                break
                            }
                        }
                    }
            }
            else{
                binding.tvUser.visibility = View.GONE
            }
        }
    }
}