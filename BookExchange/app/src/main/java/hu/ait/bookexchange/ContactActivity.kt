package hu.ait.bookexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import hu.ait.bookexchange.databinding.ActivityContactBinding
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import kotlin.concurrent.thread


class ContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding

    var launchEmailActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            if (isFormValid()) {
                val emailsubject: String = binding.etSubject.text.toString()
                val emailbody: String = binding.etMsg.text.toString()
                val emailsender: String = FirebaseAuth.getInstance().currentUser!!.email.toString()

                try {
                    sendEmail(getString(R.string.app_email), emailsender, emailsubject, emailbody)

                } catch (e: Exception) {
                    Log.d("TAG_CONTACT", e.message.toString())
                    Toast.makeText(this, "MESSAGE NOT SENT. TRY AGAIN", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if(launchEmailActivity){
            Toast.makeText(this, getString(R.string.msg_sent), Toast.LENGTH_LONG).show()
            launchEmailActivity = false
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contact, menu)
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
            R.id.action_claimed_books -> {
                startActivity(Intent(this, ClaimedBookActivity::class.java))
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

    private fun isFormValid(): Boolean {
        return when {
            binding.etSubject.text.isEmpty() -> {
                binding.etSubject.error = getString(R.string.enter_subj)
                false
            }
            binding.etMsg.text.isEmpty() -> {
                binding.etMsg.error = getString(R.string.enter_msg)
                false
            }

            else -> true
        }
    }

    fun sendEmail(recipient: String, sender: String, subject: String, msg: String) {
        // define Intent object
        // with action attribute as ACTION_SEND
        val intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, msg)

        // set type of intent
        intent.type = "message/rfc822"
        startActivity(intent)
        launchEmailActivity = true
    }
}