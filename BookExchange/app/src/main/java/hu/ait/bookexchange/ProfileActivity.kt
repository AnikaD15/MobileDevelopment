package hu.ait.bookexchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.ait.bookexchange.RegisterActivity.Companion.COLLECTION_USERS
import hu.ait.bookexchange.data.User
import hu.ait.bookexchange.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    var isEditable = false
    private lateinit var doc_key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        //get user data to prefill fields
        val userCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_USERS)

        userCollection.whereEqualTo(
            "user_id", FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var user  = document.data

                    doc_key = document.id
                    binding.tvName.setText(user.get("name").toString())
                    binding.tvEmail.setText(user.get("email").toString())
                    binding.tvPhone.setText(user.get("phone").toString())
                    binding.tvSchool.setText(user.get("school").toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, getString(R.string.profile_error), Toast.LENGTH_LONG)
            }

        binding.btnEdit.setOnClickListener {
            if(!isEditable){
                isEditable = true
                binding.btnEdit.text = getString(R.string.save)

                binding.tvName.isEnabled = isEditable
                binding.tvEmail.isEnabled = isEditable
                binding.tvPhone.isEnabled = isEditable
                binding.tvSchool.isEnabled = isEditable
            }
            else{
                if(isFormValid()){
                    isEditable = false
                    binding.tvName.isEnabled = isEditable
                    binding.tvEmail.isEnabled = isEditable
                    binding.tvPhone.isEnabled = isEditable
                    binding.tvSchool.isEnabled = isEditable
                    binding.btnEdit.text = getString(R.string.edit)

                    // modify user
                    val user = User(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        binding.tvName.text.toString(),
                        binding.tvEmail.text.toString(),
                        binding.tvPhone.text.toString().toInt(),
                        binding.tvSchool.text.toString()
                    )

                    userCollection.document(doc_key).set(user)

                    // modify authentication
                    var authUser = FirebaseAuth.getInstance().getCurrentUser()
                    authUser?.updateEmail(binding.tvEmail.text.toString())
                }
            }
        }
    }

    fun isFormValid(): Boolean {
        return when {
            binding.tvName.text.isEmpty() -> {
                binding.tvName.error = getString(R.string.enter_name)
                false
            }
            binding.tvSchool.text.isEmpty() -> {
                binding.tvSchool.error = getString(R.string.enter_school)
                false
            }
            binding.tvEmail.text.isEmpty() -> {
                binding.tvEmail.error = getString(R.string.enter_email)
                false
            }

            binding.tvPhone.text.isEmpty() -> {
                binding.tvPhone.error = getString(R.string.enter_phone)
                false
            }

            else -> true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                startActivity(Intent(this, BookListActivity::class.java))
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