package hu.ait.shoppinglist.adapter

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.ait.shoppinglist.DetailsActivity
import hu.ait.shoppinglist.R
import hu.ait.shoppinglist.ScrollingActivity
import hu.ait.shoppinglist.data.AppDatabase
import hu.ait.shoppinglist.data.Item
import hu.ait.shoppinglist.databinding.ItemRowBinding
import hu.ait.shoppinglist.touch.ItemTouchHelperCallback
import java.util.*
import kotlin.concurrent.thread

class ShoppingListAdapter : ListAdapter<Item, ShoppingListAdapter.ViewHolder>, ItemTouchHelperCallback {

    val context: Context

    companion object{
        val KEY_DETAIL_ITEM = "KEY_DETAIL_ITEM"
    }

    constructor(context: Context) : super(ItemDiffCallback()) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRowBinding = ItemRowBinding.inflate(
            LayoutInflater.from(context),
            parent, false)
        return ViewHolder(itemRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem : Item = getItem(holder.adapterPosition)
        holder.bind(currentItem)

        holder.itemRowBinding.imgIcon.setImageResource(
            currentItem.getImageResource(currentItem.categoryId))

        holder.itemRowBinding.imgIcon.setOnClickListener {
            val detailIntent = Intent((context as ScrollingActivity), DetailsActivity::class.java)
            val options = ActivityOptions
                .makeSceneTransitionAnimation((context as ScrollingActivity), holder.itemRowBinding.imgIcon, "itemDetails")
            detailIntent.putExtra(KEY_DETAIL_ITEM, currentItem) // pass your bundle data
            (context as ScrollingActivity).startActivity(detailIntent, options.toBundle())
        }

        // set delete button
        holder.itemRowBinding.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        // set view/edit button
        holder.itemRowBinding.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditDialog(currentItem)
        }

        // set check box
        holder.itemRowBinding.cbStatus.setOnClickListener{
            currentItem.isPurchased = holder.itemRowBinding.cbStatus.isChecked
            updateItem(currentItem)
        }
    }

    private fun updateItem(item: Item) {
        thread {
            AppDatabase.getInstance(context).itemDao().updateItem(item)
        }
    }

    fun deleteItem(index: Int) {
        thread {
            AppDatabase.getInstance(context).itemDao().deleteItem(getItem(index))
        }
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(val itemRowBinding: ItemRowBinding) : RecyclerView.ViewHolder(itemRowBinding.root) {
        fun bind(item: Item) {
            itemRowBinding.tvName.text = item.name
            itemRowBinding.tvPrice.text = (context as ScrollingActivity).getString(
                R.string.price, item.price)
            itemRowBinding.cbStatus.isChecked = item.isPurchased
        }
    }

}

class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}