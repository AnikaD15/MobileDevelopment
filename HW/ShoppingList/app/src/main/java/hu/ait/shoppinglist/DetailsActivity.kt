package hu.ait.shoppinglist

import android.app.ActivityOptions
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.shoppinglist.adapter.ShoppingListAdapter
import hu.ait.shoppinglist.adapter.ShoppingListAdapter.Companion.KEY_DETAIL_ITEM
import hu.ait.shoppinglist.data.Item
import hu.ait.shoppinglist.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    lateinit var binding : ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var item : Item = intent.getSerializableExtra(KEY_DETAIL_ITEM) as Item

        // set fields
        binding.detName.text = getString(R.string.item_name,item.name)
        binding.detPrice.text = getString(R.string.item_price, item.price)
        binding.detDescrip.text= getString(R.string.item_description,item.description)
        binding.detCategory.text = getString(R.string.item_category,
            resources.getStringArray(R.array.category_array)[item.categoryId])
        binding.detStatus.isChecked = item.isPurchased
        binding.imgIcon.setImageResource(item.getImageResource(item.categoryId))
    }
}