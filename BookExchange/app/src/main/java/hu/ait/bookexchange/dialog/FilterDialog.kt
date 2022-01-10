package hu.ait.bookexchange.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hu.ait.bookexchange.R
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.data.Search
import hu.ait.bookexchange.databinding.BookDialogBinding
import hu.ait.bookexchange.databinding.FilterDialogBinding

class FilterDialog : BottomSheetDialogFragment() {

    interface QueryHandler {
        fun updateSearch(newSearch: Search)
    }

    companion object {
        const val TAG = "FilterDialog"
    }

    lateinit var filterDialogBinding: FilterDialogBinding
    lateinit var filter: Search

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? = inflater.inflate(R.layout.filter_dialog, container, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        filterDialogBinding = FilterDialogBinding.inflate(layoutInflater)
        dialogBuilder.setView(filterDialogBinding.root)

        // set filter binding contents
        // book condition spinner
        val conditionAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.search_condition_array,
            android.R.layout.simple_spinner_item)

        conditionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        filterDialogBinding.spinnerConditions.adapter = conditionAdapter

        // title
        return inflater.inflate(R.layout.filter_dialog, container, false)
    }



}