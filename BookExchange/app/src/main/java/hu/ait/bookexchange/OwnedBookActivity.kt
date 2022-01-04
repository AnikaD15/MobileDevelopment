package hu.ait.bookexchange

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import hu.ait.bookexchange.BookListActivity.Companion.COLLECTION_BOOKS
import hu.ait.bookexchange.adapter.OwnedBookAdapter
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.ActivityOwnedBookBinding
import java.util.*

class OwnedBookActivity : AppCompatActivity(), BookDialog.BookHandler {

    private lateinit var binding: ActivityOwnedBookBinding
    private lateinit var ownedBookAdapter: OwnedBookAdapter
    private var listenerRegOwnedBooks: ListenerRegistration? = null

    companion object{
        val KEY_BOOK_EDIT = "KEY_BOOK_EDIT"
        val BOOK_KEY = "BOOK_KEY"
        const val PERMISSION_REQUEST_CODE = 1001
        const val CAMERA_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOwnedBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        ownedBookAdapter = OwnedBookAdapter(this,
            FirebaseAuth.getInstance().currentUser!!.uid)
        binding.recyclerOwnedBooks.adapter = ownedBookAdapter
        initFirebaseQuery()

        binding.btnAdd.setOnClickListener { view ->
            BookDialog().show(supportFragmentManager, "BOOK_DIALOG")
        }
    }

    private fun initFirebaseQuery() {
        val queryRefOwnedBooks = FirebaseFirestore.getInstance().collection(COLLECTION_BOOKS)
            .whereEqualTo("user_id", FirebaseAuth.getInstance().currentUser!!.uid)

        val eventListenerOwnedBooks = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@OwnedBookActivity, getString(R.string.error_msg, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        val book = docChange.document.toObject(Book::class.java)
                        ownedBookAdapter.addBook(book, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        ownedBookAdapter.removeBookByKey(docChange.document.id)
                    }
                }
            }
        }

        listenerRegOwnedBooks = queryRefOwnedBooks.addSnapshotListener(eventListenerOwnedBooks)
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegOwnedBooks?.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_owned_book, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                startActivity(Intent(this, BookListActivity::class.java))
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
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

    fun showEditBookDialog(book: Book, key:String) {
        val editDialog = BookDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_BOOK_EDIT, book)
        bundle.putSerializable(BOOK_KEY, key)
        editDialog.arguments = bundle
        editDialog.show(supportFragmentManager, "TAG_BOOK_EDIT")
    }

    override fun bookCreated(newBook: Book) {
        val bookCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_BOOKS
        )

        bookCollection.add(newBook)
            .addOnSuccessListener {
                Toast.makeText(this,
                    getString(R.string.book_created), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this,
                    getString(R.string.error_msg, it.message), Toast.LENGTH_LONG).show()
            }
    }

    override fun bookUpdated(editBook: Book, key:String) {
        val bookCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_BOOKS
        )
        bookCollection.document(key).set(editBook)
        ownedBookAdapter.updateBook(editBook, key)
    }
}