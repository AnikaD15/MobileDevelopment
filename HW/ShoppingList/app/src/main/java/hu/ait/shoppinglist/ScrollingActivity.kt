package hu.ait.shoppinglist

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import hu.ait.shoppinglist.adapter.ShoppingListAdapter
import hu.ait.shoppinglist.data.AppDatabase
import hu.ait.shoppinglist.data.Item
import hu.ait.shoppinglist.databinding.ActivityScrollingBinding
import hu.ait.shoppinglist.dialog.ItemDialog
import hu.ait.shoppinglist.touch.ShoppingListTouchCallback
import kotlin.concurrent.thread

class ScrollingActivity : AppCompatActivity(), ItemDialog.ItemHandler {
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var adapter: ShoppingListAdapter
    companion object{
        const val KEY_ITEM_EDIT = "KEY_ITEM_EDIT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        initRecyclerView()

    }

    private fun initRecyclerView() {
        adapter = ShoppingListAdapter(this)
        binding.recylerItem.adapter = adapter

        val shoppingCallbackList = ShoppingListTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(shoppingCallbackList)
        itemTouchHelper.attachToRecyclerView(binding.recylerItem)

        val itemLiveData = AppDatabase.getInstance(this).itemDao().getAllItems()

        // observe is called when table is changed
        itemLiveData.observe(this, Observer { items ->
            adapter.submitList(items)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // create button not working
            R.id.action_create -> {
                ItemDialog().show(supportFragmentManager, "ITEM_DIALOG")
            }
            R.id.action_delete_all -> {
                thread {
                    AppDatabase.getInstance(this@ScrollingActivity).itemDao().deleteAllItems()
                }
            }
        }
        return true
    }
    public fun showEditDialog(itemEdit: Item) {
        val editDialog = ItemDialog()
        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_EDIT, itemEdit)
        editDialog.arguments = bundle
        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

    override fun itemCreated(newItem: Item) {
        thread {
            AppDatabase.getInstance(this).itemDao().insertItem(newItem)
        }
    }

    override fun itemUpdated(editedItem: Item) {
        thread {
            AppDatabase.getInstance(this).itemDao().updateItem(editedItem)
        }
    }
}