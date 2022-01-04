package hu.ait.todorecyclerview

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import hu.ait.todorecyclerview.databinding.ActivityScrollingBinding
import hu.ait.todorecyclerview.adapter.ToDoRecyclerAdapter
import hu.ait.todorecyclerview.data.Todo
import hu.ait.todorecyclerview.dialogue.TodoDialog
import hu.ait.todorecyclerview.touch.ToDoRecyclerTouchCallback

class ScrollingActivity : AppCompatActivity(), TodoDialog.TodoHandler {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var adapter: ToDoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            TodoDialog().show(supportFragmentManager, "TODO DIALOG")
        }

        adapter = ToDoRecyclerAdapter(this)
        binding.recylerTodo.adapter = adapter

        //val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        //binding.recylerTodo.addItemDecoration(divider)

        //binding.recylerTodo.layoutManager = GridLayoutManager(this, 2)
        binding.recylerTodo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val touchCallbackList = ToDoRecyclerTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(touchCallbackList)
        itemTouchHelper.attachToRecyclerView(binding.recylerTodo)
    }

    // need to overwrite interface methods
    override fun todoCreated(todo: Todo) {
        adapter.addTodo(todo)

        Snackbar.make(binding.root, "Todo created", Snackbar.LENGTH_LONG).setAction("Undo"){
            adapter.deleteTodo(adapter.todoItems.lastIndex)
        }.show()
    }


}