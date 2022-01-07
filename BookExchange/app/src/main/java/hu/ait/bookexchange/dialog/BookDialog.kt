package hu.ait.bookexchange

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import hu.ait.bookexchange.OwnedBookActivity.Companion.CAMERA_REQUEST_CODE
import hu.ait.bookexchange.OwnedBookActivity.Companion.KEY_BOOK_EDIT
import hu.ait.bookexchange.OwnedBookActivity.Companion.PERMISSION_REQUEST_CODE
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.databinding.BookDialogBinding
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

class BookDialog: DialogFragment() {
    interface BookHandler {
        fun bookCreated(newBook: Book)
        fun bookUpdated(editBook: Book, key:String)
    }

    lateinit var bookHandler: BookHandler
    lateinit var bookDialogBinding: BookDialogBinding
    lateinit var bookEdit: Book
    var isEditMode = false
    var imgUrl = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BookHandler){
            bookHandler = context
        }
        else {
            throw RuntimeException(
                getString(R.string.bookhandler_exception))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set dialog title
        if (arguments != null && requireArguments().containsKey(
                OwnedBookActivity.KEY_BOOK_EDIT)) {
            isEditMode = true
            dialogBuilder.setTitle(getString(R.string.edit_book))
        } else {
            isEditMode = false
            dialogBuilder.setTitle(getString(R.string.new_book))
        }

        bookDialogBinding = BookDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(bookDialogBinding.root)

        // book condition spinner
        val conditionAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.condition_array,
            android.R.layout.simple_spinner_item)

        conditionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        bookDialogBinding.spinnerConditions.adapter = conditionAdapter

        // load image
        bookDialogBinding.ivBook.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                CAMERA_REQUEST_CODE)

            requestNeededPermission()
        }

        // set dialog contents
        if (isEditMode) {
            bookEdit =
                requireArguments().getSerializable(
                    OwnedBookActivity.KEY_BOOK_EDIT) as Book

            if (bookEdit.imgUrl.isNotEmpty()) {
                Glide.with(requireActivity()).load(bookEdit.imgUrl).into(bookDialogBinding.ivBook)
                imgUrl = bookEdit.imgUrl
            }

            bookDialogBinding.etTitle.setText(bookEdit.title)
            bookDialogBinding.etPrice.setText(bookEdit.price.toString())
            bookDialogBinding.etAuthor.setText(bookEdit.author)
            bookDialogBinding.spinnerConditions.setSelection(bookEdit.condition)
        }

        // set dialog buttons
        if(isEditMode){
            dialogBuilder.setPositiveButton(getString(R.string.save)) {
                    dialog, which ->
            }
        }
        else{
            dialogBuilder.setPositiveButton(getString(R.string.create)) {
                    dialog, which ->
            }
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel)) {
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
                    getString(R.string.camera_prompt), Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
        }
    }

    var uploadBitmap: Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            uploadBitmap = data!!.extras!!.get("data") as Bitmap
            bookDialogBinding.ivBook.setImageBitmap(uploadBitmap)
            getImageURL()
        }
    }

    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (bookDialogBinding.etTitle.text.isEmpty()){
                bookDialogBinding.etTitle.error = getString(R.string.empty_field_error)
            }

            if (bookDialogBinding.etAuthor.text.isEmpty()){
                bookDialogBinding.etAuthor.error = getString(R.string.empty_field_error)
            }

            if (bookDialogBinding.etPrice.text.isEmpty()){
                bookDialogBinding.etPrice.error = getString(R.string.empty_field_error)
            }

            if (bookDialogBinding.etPrice.text.toString().toFloat() < 0){
                bookDialogBinding.etPrice.error = getString(R.string.neg_price_error)
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
            imgUrl
        ))
    }

    fun handleBookEdit() {
        bookEdit = (arguments?.getSerializable(
            OwnedBookActivity.KEY_BOOK_EDIT) as Book).copy(
            user_id = bookEdit.user_id,
            author = bookDialogBinding.etAuthor.text.toString(),
            title = bookDialogBinding.etTitle.text.toString(),
            condition = bookDialogBinding.spinnerConditions.selectedItemPosition,
            price = bookDialogBinding.etPrice.text.toString().toFloat(),
            imgUrl = imgUrl,
            isClaimed = bookEdit.isClaimed,
            claimedBy = bookEdit.claimedBy,
            claimEndDate = bookEdit.claimEndDate
        )

        bookHandler.bookUpdated(bookEdit, arguments?.getSerializable(
            OwnedBookActivity.BOOK_KEY) as String
        )
    }

    private fun getImageURL() {
        val baos = ByteArrayOutputStream()
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().getReference()
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        val newImagesRef = storageRef.child("images/$newImage")

        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(requireActivity(), exception.message, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                newImagesRef.downloadUrl.addOnCompleteListener(object: OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        imgUrl = task.result.toString()
                    }
                })
            }
    }
}