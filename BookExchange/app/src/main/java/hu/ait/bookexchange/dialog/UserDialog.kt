package hu.ait.bookexchange

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import hu.ait.bookexchange.OwnedBookActivity.Companion.CAMERA_REQUEST_CODE
import hu.ait.bookexchange.OwnedBookActivity.Companion.KEY_BOOK_EDIT
import hu.ait.bookexchange.OwnedBookActivity.Companion.PERMISSION_REQUEST_CODE
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.data.User
import hu.ait.bookexchange.databinding.BookDialogBinding
import hu.ait.bookexchange.databinding.UserDialogBinding
import java.util.*

class UserDialog: DialogFragment() {
    lateinit var userDialogBinding: UserDialogBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        userDialogBinding = UserDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(userDialogBinding.root)
        val user = requireArguments().getSerializable(
            ClaimedBookActivity.KEY_USER_VIEW) as User

        userDialogBinding.tvName.text = user.name
        userDialogBinding.tvEmail.text = user.email
        userDialogBinding.tvPhone.text = user.phone.toString()
        userDialogBinding.tvSchool.text = user.school

        dialogBuilder.setNegativeButton(getString(R.string.cancel)) {
                dialog, which ->
        }

        return dialogBuilder.create()
    }
}