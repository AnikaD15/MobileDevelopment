package hu.ait.bookexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.ait.bookexchange.RegisterActivity.Companion.COLLECTION_USERS
import hu.ait.bookexchange.data.User
import hu.ait.bookexchange.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    var isEditable = false
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get user data to prefill fields
    //        val userCollection = FirebaseFirestore.getInstance().collection(
//            COLLECTION_USERS)

//        var user = userCollection.whereEqualTo(
//            "id", FirebaseAuth.getInstance().currentUser!!.uid)

//        binding.tvName.setText(user.name)
//        binding.tvEmail.setText(user.email)
//        binding.tvPhone.setText(user.phone.toString())
//        binding.tvSchool.setText(user.school)

        binding.btnEdit.setOnClickListener {
            if(!isEditable){
                isEditable = true
                binding.btnEdit.text = "Save"

                binding.tvName.isEnabled = isEditable
                binding.tvEmail.isEnabled = isEditable
                binding.tvPhone.isEnabled = isEditable
                binding.tvSchool.isEnabled = isEditable
            }
            else{
                if(RegisterActivity().isFormValid()){
                    isEditable = false
                    binding.btnEdit.text = "Edit"

                    // modify user
                    val user = User(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        binding.tvName.text.toString(),
                        binding.tvEmail.text.toString(),
                        binding.tvPhone.text.toString().toInt(),
                        binding.tvSchool.text.toString()
                    )
                }
            }
        }
    }
}