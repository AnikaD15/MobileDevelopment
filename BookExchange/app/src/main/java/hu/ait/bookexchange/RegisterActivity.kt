package hu.ait.bookexchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.ait.bookexchange.data.User
import hu.ait.bookexchange.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val db = Firebase.firestore

    companion object{
        const val COLLECTION_USERS = "users"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        if(isFormValid()){

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.etEmail.text.toString(), binding.etPassword.text.toString()
            ).addOnSuccessListener {
                Toast.makeText(this, getString(R.string.account_created), Toast.LENGTH_LONG).show()

                val user = User(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPhone.text.toString().toInt(),
                    binding.etSchool.text.toString()
                )

                db.collection(COLLECTION_USERS)
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        startActivity(Intent(this, BookListActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,
                            "Error ${e.message}", Toast.LENGTH_LONG).show()
                    }

            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun isFormValid(): Boolean {
        return when {
            binding.etName.text.isEmpty() -> {
                binding.etName.error = getString(R.string.enter_name)
                false
            }
            binding.etSchool.text.isEmpty() -> {
                binding.etSchool.error = getString(R.string.enter_school)
                false
            }
            binding.etEmail.text.isEmpty() -> {
                binding.etEmail.error = getString(R.string.enter_email)
                false
            }

            binding.etPhone.text.isEmpty() -> {
                binding.etPhone.error = getString(R.string.enter_phone)
                false
            }

            binding.etPassword.text.isEmpty() -> {
                binding.etPassword.error = getString(R.string.enter_password)
                false
            }

            else -> true
        }
    }
}