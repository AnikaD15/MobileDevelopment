package hu.ait.shoppinglist.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.fragment.app.DialogFragment
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import hu.ait.shoppinglist.R
import hu.ait.shoppinglist.ScrollingActivity
import hu.ait.shoppinglist.data.Item
import hu.ait.shoppinglist.databinding.ItemDialogBinding
import java.util.*

class ItemDialog : DialogFragment(){
    interface ItemHandler {
        fun itemCreated(newItem: Item)
        fun itemUpdated(editedItem: Item)
    }

    lateinit var itemHandler: ItemHandler
    lateinit var itemDialogBinding: ItemDialogBinding
    var isEditMode = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ItemHandler){
            itemHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the ItemHandler interface.")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        if (arguments != null && requireArguments().containsKey(
                ScrollingActivity.KEY_ITEM_EDIT)) {
            isEditMode = true
            dialogBuilder.setTitle("Edit Item")
        } else {
            isEditMode = false
            dialogBuilder.setTitle("New Item")
        }

        itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(itemDialogBinding.root)

        // spinner for categories
        val categoryAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.category_array,
            android.R.layout.simple_spinner_item)

        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        itemDialogBinding.spinnerCategories.adapter = categoryAdapter

        if (isEditMode) {
            val itemEdit =
                requireArguments().getSerializable(
                    ScrollingActivity.KEY_ITEM_EDIT) as Item

            itemDialogBinding.etName.setText(itemEdit.name)
            itemDialogBinding.etPrice.setText(itemEdit.price.toString())
            itemDialogBinding.etDescrip.setText(itemEdit.description)
            itemDialogBinding.cbItem.isChecked = itemEdit.isPurchased
            itemDialogBinding.spinnerCategories.setSelection(itemEdit.categoryId)
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

    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (itemDialogBinding.etName.text.isEmpty()){
                itemDialogBinding.etName.error = "This field can not be empty!"
            }

            if (itemDialogBinding.etDescrip.text.isEmpty()){
                itemDialogBinding.etDescrip.error = "This field can not be empty!"
            }

            if (itemDialogBinding.etName.text.isNotEmpty() &&
                itemDialogBinding.etPrice.text.isNotEmpty() &&
                itemDialogBinding.etDescrip.text.isNotEmpty()) {
                if (isEditMode) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            }
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(Item(
            null,
            itemDialogBinding.spinnerCategories.selectedItemId.toInt(),
            itemDialogBinding.etName.text.toString(),
            itemDialogBinding.etDescrip.text.toString(),
            itemDialogBinding.etPrice.text.toString().toFloat(),
            itemDialogBinding.cbItem.isChecked
        ))
    }

    fun handleItemEdit() {
        val itemEdit = (arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_EDIT) as Item).copy(
            categoryId = itemDialogBinding.spinnerCategories.selectedItemId.toInt(),
            name = itemDialogBinding.etName.text.toString(),
            description = itemDialogBinding.etDescrip.text.toString(),
            price = itemDialogBinding.etPrice.text.toString().toFloat(),
            isPurchased = itemDialogBinding.cbItem.isChecked
        )

        itemHandler.itemUpdated(itemEdit)
    }
}