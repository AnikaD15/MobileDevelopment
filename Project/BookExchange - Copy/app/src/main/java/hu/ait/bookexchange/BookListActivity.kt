package hu.ait.bookexchange

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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
        binding.toolbarLayout.title = "Book List - Browse"
    }

    private fun initFirebaseQuery() {
        val queryRefBooks = FirebaseFirestore.getInstance().collection(COLLECTION_BOOKS)

        val eventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@BookListActivity, "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        // converts the document from the "posts" collection into a Post object
                        val book = docChange.document.toObject(Book::class.java)
                        // id is not stored in the document

                        adapter.addBook(book, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        adapter.removeBookByKey(docChange.document.id)
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
            R.id.action_books -> {
                startActivity(Intent(this, UserBookListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}