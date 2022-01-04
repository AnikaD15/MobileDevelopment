package hu.ait.bookexchange

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import hu.ait.bookexchange.adapter.BookListAdapter
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.ActivityBookListBinding

class BookListActivity : AppCompatActivity(){

    private lateinit var binding: ActivityBookListBinding
    private lateinit var adapter: BookListAdapter
    private var listenerReg: ListenerRegistration? = null

    companion object{
        const val COLLECTION_BOOKS = "books"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BookListAdapter(this,
            FirebaseAuth.getInstance().currentUser!!.uid)
        binding.recyclerBooks.adapter = adapter

        initFirebaseQuery()

        setSupportActionBar(findViewById(R.id.toolbar))

//        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.search -> {
//                    // Handle search icon press
//                    true
//                }
//                R.id.more -> {
//                    // Handle more item (inside overflow menu) press
//                    // menuInflater.inflate(R.menu.menu_book_list, )
//                    true
//                }
//                else -> false
//            }
//        }
    }

    private fun initFirebaseQuery() {
        val queryRefBooks = FirebaseFirestore.getInstance().collection(COLLECTION_BOOKS)
            .whereNotEqualTo("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
            .whereEqualTo("claimed", false)

        val eventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@BookListActivity, getString(R.string.error_msg, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("TAG_INDEX", getString(R.string.error_msg, e.message))
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        // converts the document from the "posts" collection into a Post object
                        val book = docChange.document.toObject(Book::class.java)
                        adapter.addBook(book, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        adapter.removeBookByKey(docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.MODIFIED) {
                        val book = docChange.document.toObject(Book::class.java)
                        adapter.updateBook(book, docChange.document.id)
                    }
                }
            }
        }

        listenerReg = queryRefBooks.addSnapshotListener(eventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerReg?.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_book_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_owned_books -> {
                startActivity(Intent(this, OwnedBookActivity::class.java))
                true
            }
            R.id.action_claimed_books -> {
                startActivity(Intent(this, ClaimedBookActivity::class.java))
                true
            }
            R.id.action_contact -> {
                startActivity(Intent(this, ContactActivity::class.java))
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}