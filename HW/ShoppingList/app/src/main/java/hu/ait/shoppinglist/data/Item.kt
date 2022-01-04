package hu.ait.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.ait.shoppinglist.R
import java.io.Serializable

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "categoryId") var categoryId: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "price") var price: Float,
    @ColumnInfo(name = "isPurchased") var isPurchased: Boolean) : Serializable {

    companion object {
        const val FOOD: Int = 0
        const val CLOTHES: Int = 1
        const val ENTERTAINMENT: Int = 2
        const val OTHER: Int = 3
    }

    fun getImageResource(categoryId: Int): Int {
        return when (categoryId) {
            FOOD -> {
                R.drawable.food
            }
            CLOTHES -> {
                R.drawable.clothes
            }
            ENTERTAINMENT -> {
                R.drawable.entertainment
            }
            else -> {
                R.drawable.other
            }
        }
    }
}

