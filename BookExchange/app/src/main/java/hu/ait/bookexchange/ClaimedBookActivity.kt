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
import hu.ait.bookexchange.adapter.ClaimedBookAdapter
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.ActivityClaimedBookBinding
import android.view.Menu
import android.view.MenuItem
import hu.ait.bookexchange.data.User
import java.util.*

class ClaimedBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClaimedBookBinding
    private lateinit var claimedBookAdapter: ClaimedBookAdapter
    private var listenerRegClaimedBooks: ListenerRegistration? = null

    companion object{
        val KEY_USER_VIEW = "KEY_USER_VIEW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClaimedBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        claimedBookAdapter = ClaimedBookAdapter(this,
            FirebaseAuth.getInstance().currentUser!!.uid)
        binding.recyclerClaimedBooks.adapter = claimedBookAdapter

        initFirebaseQuery()
    }

    private fun initFirebaseQuery() {
        val queryRefClaimedBooks = FirebaseFirestore.getInstance().collection(COLLECTION_BOOKS)
            .whereEqualTo("claimedBy", FirebaseAuth.getInstance().currentUser!!.uid)

        val eventListenerClaimedBooks = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(
                        this@ClaimedBookActivity, getString(R.string.error_msg, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        val book = docChange.document.toObject(Book::class.java)
                        claimedBookAdapter.addBook(book, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        claimedBookAdapter.removeBookByKey(docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.MODIFIED) {
                        val book = docChange.document.toObject(Book::class.java)
                        claimedBookAdapter.updateBook(book, docChange.document.id)
                    }
                }
            }
        }

        listenerRegClaimedBooks = queryRefClaimedBooks.addSnapshotListener(eventListenerClaimedBooks)
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegClaimedBooks?.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_claimed_book, menu)
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
            R.id.action_owned_books -> {
                startActivity(Intent(this, OwnedBookActivity::class.java))
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

    fun showUserDialog(user: User) {
        val viewDialog = UserDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_USER_VIEW, user)
        viewDialog.arguments = bundle
        viewDialog.show(supportFragmentManager, "TAG_USER_VIEW")
    }
}