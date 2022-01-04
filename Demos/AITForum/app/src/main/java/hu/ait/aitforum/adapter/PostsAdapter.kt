package hu.ait.aitforum.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.aitforum.CreatePostActivity
import hu.ait.aitforum.ScrollingActivity
import hu.ait.aitforum.data.Post
import hu.ait.aitforum.databinding.PostRowBinding

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.ViewHolder>  {

    lateinit var context: Context
    var  postsList = mutableListOf<Post>()
    var  postKeys = mutableListOf<String>()

    lateinit var currentUid: String

    constructor(context: Context, uid: String) : super() {
        this.context = context
        this.currentUid = uid
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostRowBinding.inflate((context as ScrollingActivity).layoutInflater,
            parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[holder.adapterPosition]
        holder.bind(post)

        holder.binding.btnDelete.setOnClickListener {
            removePost(holder.adapterPosition)
        }
    }

    // when I remove the post object
    private fun removePost(index: Int) {
        FirebaseFirestore.getInstance().collection(CreatePostActivity.COLLECTION_POSTS).document(
            postKeys[index]
        ).delete()

        postsList.removeAt(index)
        postKeys.removeAt(index)
        notifyItemRemoved(index)
    }


    // when somebody else removes an object
    fun removePostByKey(key: String) {
        val index = postKeys.indexOf(key)
        if (index != -1) {
            postsList.removeAt(index)
            postKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    public fun addPost(post:Post, key:String){
        postsList.add(post)
        postKeys.add(key)
        notifyItemInserted(postsList.lastIndex)
    }


    inner class ViewHolder(var binding: PostRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post){
            binding.tvAuthor.text = post.author
            binding.tvTitle.text = post.title
            binding.tvBody.text = post.body

            if (post.uid == currentUid) {
                binding.btnDelete.visibility = View.VISIBLE
            } else {
                binding.btnDelete.visibility = View.GONE
            }
        }
    }
}