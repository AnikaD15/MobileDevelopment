package hu.bme.todorecyclerview.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


// table name is by default the class name
@Entity(tableName = "todoTable")
data class Todo(
    // id is long by default
    @PrimaryKey(autoGenerate = true) var _id: Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "createDate") var createDate: String,
    @ColumnInfo(name = "done") var done: Boolean) : Serializable