package hu.ait.bookexchange

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.ait.bookexchange.BookListActivity.Companion.COLLECTION_BOOKS
import hu.ait.bookexchange.adapter.OwnedBookAdapter
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.ActivityUserBooksBinding
import java.util.*

class UserBookListActivity : AppCompatActivity(), BookDialog.BookHandler {

    private lateinit var binding: ActivityUserBooksBinding
    private lateinit var ownedBookAdapter: OwnedBookAdapter
    private var listenerRegOwnedBooks: ListenerRegistration? = null

    companion object{
        val KEY_BOOK_EDIT = "KEY_BOOK_EDIT"
        const val PERMISSION_REQUEST_CODE = 1001
        const val CAMERA_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = "Your Books"

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
        queryRefOwnedBooks.whereEqualTo("student_id", FirebaseAuth.getInstance().currentUser!!.uid)

        val eventListenerOwnedBooks = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@UserBookListActivity, "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        // converts the document from the "posts" collection into a Post object
                        val book = docChange.document.toObject(Book::class.java)
                        // id is not stored in the document
                        ownedBookAdapter.addBook(book, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        ownedBookAdapter.removeBookByKey(docChange.document.id)
                    }
                }

            }
        }

        listenerRegOwnedBooks = queryRefOwnedBooks.addSnapshotListener(eventListenerOwnedBooks)
    }

    fun showEditBookDialog(book: Book, key:String) {
        val editDialog = BookDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_BOOK_EDIT, book)
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
                    "BOOK CREATED", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this,
                    "Error ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun bookUpdated(editBook: Book) {
        val bookCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_BOOKS
        )

    }
}