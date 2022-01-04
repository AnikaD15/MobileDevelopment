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
import hu.ait.bookexchange.UserBookListActivity.Companion.CAMERA_REQUEST_CODE
import hu.ait.bookexchange.UserBookListActivity.Companion.KEY_BOOK_EDIT
import hu.ait.bookexchange.UserBookListActivity.Companion.PERMISSION_REQUEST_CODE
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.BookDialogBinding
import java.util.*

class BookDialog: DialogFragment() {
    interface BookHandler {
        fun bookCreated(newBook: Book)
        fun bookUpdated(editBook: Book)
    }

    lateinit var bookHandler: BookHandler
    lateinit var bookDialogBinding: BookDialogBinding
    lateinit var bookEdit: Book
    var isEditMode = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BookHandler){
            bookHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the BookHandler interface.")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        if (arguments != null && requireArguments().containsKey(
                UserBookListActivity.KEY_BOOK_EDIT)) {
            isEditMode = true
            dialogBuilder.setTitle("Edit Book")
        } else {
            isEditMode = false
            dialogBuilder.setTitle("New Book")
        }

        bookDialogBinding = BookDialogBinding.inflate(requireActivity().layoutInflater)
        dialogBuilder.setView(bookDialogBinding.root)

        val conditionAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.condition_array,
            android.R.layout.simple_spinner_item)

        conditionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        bookDialogBinding.spinnerConditions.adapter = conditionAdapter

        if (isEditMode) {
            bookEdit =
                requireArguments().getSerializable(
                    UserBookListActivity.KEY_BOOK_EDIT) as Book

            if (bookEdit.imgUrl.isNotEmpty()) {
                Glide.with(requireActivity()).load(bookEdit.imgUrl).into(bookDialogBinding.ivBook)
            } else {
                bookDialogBinding.ivBook.setImageResource(R.drawable.missing_photo)
            }
            bookDialogBinding.etTitle.setText(bookEdit.title)
            bookDialogBinding.etPrice.setText(bookEdit.price.toString())
            bookDialogBinding.etAuthor.setText(bookEdit.author)
            bookDialogBinding.spinnerConditions.setSelection(bookEdit.condition)

            bookDialogBinding.ivBook.setOnClickListener {
                startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    CAMERA_REQUEST_CODE)

                requestNeededPermission()
            }


        }

        if(isEditMode){
            dialogBuilder.setPositiveButton("Save") {
                    dialog, which ->
            }
        }
        else{
            dialogBuilder.setPositiveButton("Create") {
                    dialog, which ->
            }
        }

        dialogBuilder.setNegativeButton("Cancel") {
                dialog, which ->
        }

        return dialogBuilder.create()
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.CAMERA)) {
                Toast.makeText(requireActivity(),
                    "Camera is needed to take image of book", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (bookDialogBinding.etTitle.text.isEmpty()){
                bookDialogBinding.etTitle.error = "This field can not be empty!"
            }

            if (bookDialogBinding.etAuthor.text.isEmpty()){
                bookDialogBinding.etAuthor.error = "This field can not be empty!"
            }

            if (bookDialogBinding.etPrice.text.isEmpty()){
                bookDialogBinding.etPrice.error = "This field can not be empty!"
            }

            if (bookDialogBinding.etTitle.text.isNotEmpty() &&
                bookDialogBinding.etPrice.text.isNotEmpty() &&
                bookDialogBinding.etAuthor.text.isNotEmpty()) {
                if (isEditMode) {
                    handleBookEdit()
                } else {
                    handleBookCreate()
                }

                dialog.dismiss()
            }
        }
    }

    private fun handleBookCreate() {
        bookHandler.bookCreated(Book(
            FirebaseAuth.getInstance().currentUser!!.uid,
            bookDialogBinding.etAuthor.text.toString(),
            bookDialogBinding.etTitle.text.toString(),
            bookDialogBinding.spinnerConditions.selectedItemPosition,
            bookDialogBinding.etPrice.text.toString().toFloat(),
            "demo",
            false,
            "demo",
        ))
    }

    fun handleBookEdit() {
        bookEdit = (arguments?.getSerializable(
            UserBookListActivity.KEY_BOOK_EDIT) as Book).copy(
            user_id = bookEdit.user_id,
            author = bookEdit.author,
            title = bookEdit.title,
            condition = bookEdit.condition,
            price = bookEdit.price,
            imgUrl = bookEdit.imgUrl,
            isClaimed = bookEdit.isClaimed,
            claimedBy = bookEdit.claimedBy,
        )

        bookHandler.bookUpdated(bookEdit)
    }
}