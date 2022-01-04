package hu.ait.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDAO {
    @Query("SELECT * FROM item")
    fun getAllItems(): LiveData<List<Item>>

    @Insert
    fun insertItem(items: Item)

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(items: Item)

    @Query("DELETE FROM item")
    fun deleteAllItems()
}