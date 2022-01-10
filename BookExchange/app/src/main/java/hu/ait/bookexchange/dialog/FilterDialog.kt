package hu.ait.bookexchange.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hu.ait.bookexchange.R
import hu.ait.bookexchange.data.Book
import hu.ait.bookexchange.data.Search

class FilterDialog : BottomSheetDialogFragment() {

    interface QueryHandler {
        fun updateSearch(newSearch: Search)
    }

    companion object {
        const val TAG = "FilterDialog"
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
        return inflater.inflate(R.layout.filter_dialog, container, false)
    }



}