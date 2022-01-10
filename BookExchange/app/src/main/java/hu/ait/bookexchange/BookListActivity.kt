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
import hu.ait.bookexchange.data.Search
import hu.ait.bookexchange.data.Search.Companion.NONE
import hu.ait.bookexchange.databinding.ActivityBookListBinding
import hu.ait.bookexchange.dialog.FilterDialog

class BookListActivity : AppCompatActivity(), FilterDialog.QueryHandler{

    private lateinit var binding: ActivityBookListBinding
    private lateinit var adapter: BookListAdapter
    private var listenerReg: ListenerRegistration? = null
    var filter = Search()

    companion object{
        const val KEY_FILTER = "KEY_FILTER"
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
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // app bar options
            R.id.search ->{
                val filterDialog = FilterDialog()
                val bundle = Bundle()
                bundle.putSerializable(BookListActivity.KEY_FILTER, filter)
                filterDialog.arguments = bundle
                filterDialog.show(supportFragmentManager, FilterDialog.TAG)
                true
            }

            // more actions
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

    override fun updateSearch(search: Search) {
        var queryRefBooks = FirebaseFirestore.getInstance().collection(COLLECTION_BOOKS)

        if(!search.title.isNullOrEmpty()){
            queryRefBooks.whereEqualTo("title", search.title)
        }

        if(!search.author.isNullOrEmpty()){
            queryRefBooks.whereEqualTo("author", search.author)
        }

        if(search.condition != NONE){
            queryRefBooks.whereEqualTo("condition", search.condition)
        }

        if(search.minPrice > 0 || search.maxPrice < Float.MAX_VALUE){
            queryRefBooks.whereGreaterThan("price", search.minPrice)
                .whereLessThan("price", search.maxPrice)
        }

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
}