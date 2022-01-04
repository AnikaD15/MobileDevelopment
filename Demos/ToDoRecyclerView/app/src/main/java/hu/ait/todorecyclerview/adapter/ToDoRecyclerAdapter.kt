package hu.ait.todorecyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.ait.todorecyclerview.R
import hu.ait.todorecyclerview.data.Todo
import hu.ait.todorecyclerview.databinding.TodoRowBinding
import hu.ait.todorecyclerview.touch.TodoTouchHelperCallback
import java.util.*

class ToDoRecyclerAdapter: RecyclerView.Adapter<ToDoRecyclerAdapter.ViewHolder>, TodoTouchHelperCallback {
    val context: Context

    // items should come from database
    var todoItems = mutableListOf<Todo>(
        Todo("Do the dishes", "2020. 10. 11.", false),
        Todo("Do the dishes", "2020. 10. 11.", false)
    )

    constructor(context: Context): super(){
        this.context = context
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }

    // creates new item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoRowBinding = TodoRowBinding.inflate(LayoutInflater.from(context),
            parent, false)
        return ViewHolder(todoRowBinding)
    }

    // brings list item into focus
    // called for every item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTodo = todoItems[position]
        holder.bind(currentTodo)

        // every button has its own adapter position
        holder.todoBinding.btnDel.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }
    }

    fun addTodo(newTodo: Todo){
        todoItems.add(newTodo)
        notifyItemChanged(todoItems.lastIndex)
    }

    fun deleteTodo(index: Int) {
        todoItems.removeAt(index)
        //notifyDataSetChanged()
        notifyItemRemoved(index)
    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(todoItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(val todoBinding: TodoRowBinding) : RecyclerView.ViewHolder(todoBinding.root) {
        fun bind(todo: Todo) {
            todoBinding.tvDate.text = todo.createDate
            todoBinding.cbDone.text = todo.title
            todoBinding.cbDone.isChecked = todo.done
        }
    }
}