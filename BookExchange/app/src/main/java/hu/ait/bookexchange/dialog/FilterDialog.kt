package hu.ait.bookexchange.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hu.ait.bookexchange.BookDialog
import hu.ait.bookexchange.BookListActivity
import hu.ait.bookexchange.OwnedBookActivity
import hu.ait.bookexchange.R
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.data.Search
import hu.ait.bookexchange.databinding.BookDialogBinding
import hu.ait.bookexchange.databinding.FilterDialogBinding

import android.widget.LinearLayout




class FilterDialog : BottomSheetDialogFragment() {

    interface QueryHandler {
        fun updateSearch(newSearch: Search)
    }

    companion object {
        const val TAG = "FilterDialog"
    }

    lateinit var queryHandler: QueryHandler
    lateinit var filterDialogBinding: FilterDialogBinding
    lateinit var filter: Search

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FilterDialog.QueryHandler){
            queryHandler = context
        }
        else {
            throw RuntimeException(
                getString(R.string.queryhandler_exception))
        }
    }

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
//        val bottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog.setContentView(R.layout.filter_dialog)
//        bottomSheetDialog.show()

        var v =  inflater.inflate(R.layout.filter_dialog, container, false)
        //val dialogBuilder = AlertDialog.Builder(requireContext())
        filterDialogBinding = FilterDialogBinding.inflate(layoutInflater)
        //dialogBuilder.setView(filterDialogBinding.root)

        // set filter binding contents
        filter =
            requireArguments().getSerializable(
                BookListActivity.KEY_FILTER) as Search

        // title
        filterDialogBinding.etTitle.setText(filter.title)
        // author
        filterDialogBinding.etAuthor.setText(filter.author)
        // prices
        filterDialogBinding.etMinPrice.setText(filter.minPrice.toString())
        filterDialogBinding.etMaxPrice.setText(filter.maxPrice.toString())
        // book condition spinner
        val conditionAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.search_condition_array,
            android.R.layout.simple_spinner_item)

        conditionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        filterDialogBinding.spinnerConditions.adapter = conditionAdapter

        // button
        filterDialogBinding.btnSearch.setOnClickListener {
            queryHandler.updateSearch(filter)
        }
        return v
    }
}