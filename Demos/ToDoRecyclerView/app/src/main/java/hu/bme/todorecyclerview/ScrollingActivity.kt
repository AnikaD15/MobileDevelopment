package hu.bme.todorecyclerview

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import hu.bme.todorecyclerview.adapter.TodoRecyclerAdapter
import hu.bme.todorecyclerview.data.AppDatabase
import hu.bme.todorecyclerview.data.Todo
import hu.bme.todorecyclerview.databinding.ActivityScrollingBinding
import hu.bme.todorecyclerview.dialog.TodoDialog
import hu.bme.todorecyclerview.touch.TodoRecyclerTouchCallback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import kotlin.concurrent.thread

class ScrollingActivity : AppCompatActivity(), TodoDialog.TodoHandler {

    companion object{
        const val KEY_TODO_EDIT = "KEY_TODO_EDIT"
    }

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var adapter: TodoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            TodoDialog().show(supportFragmentManager, "TODO_DIALOG")
        }

        initRecyclerView()

        if(!wasAlreadyStarted()){
            MaterialTapTargetPrompt.Builder(this@ScrollingActivity)
                .setTarget(binding.fab)
                .setPrimaryText("New Todo Item")
                .setSecondaryText("Tap here to create new todo item")
                .show()

            saveThatAppWasStarted()
        }
    }

    private fun initRecyclerView() {
        adapter = TodoRecyclerAdapter(this)
        binding.recylerTodo.adapter = adapter

        val touchCallbakList = TodoRecyclerTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(touchCallbakList)
        itemTouchHelper.attachToRecyclerView(binding.recylerTodo)

        val todoLiveData = AppDatabase.getInstance(this).todoDao().getAllTodo()

        // observe is called when table is changed
        todoLiveData.observe(this, Observer { items ->
            adapter.submitList(items)
        })
    }

    private fun saveThatAppWasStarted() {
        val sharedPref = getSharedPreferences("PREF_DEFAULT", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("KEY_STARTED", true)
        editor.commit()
    }

    private fun wasAlreadyStarted() : Boolean {
        // file is private
        val sharedPref = getSharedPreferences("PREF_DEFAULT", MODE_PRIVATE)
        return sharedPref.getBoolean("KEY_STARTED", false)
    }

    public fun showEditDialog(todoToEdit: Todo) {
        val editDialog = TodoDialog()

        // key value storage
        // bundle can only store primitives
        val bundle = Bundle()
        bundle.putSerializable(KEY_TODO_EDIT, todoToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }


    override fun todoCreated(newTodo: Todo) {
        thread {
            // need to update id here so delete function will work
            AppDatabase.getInstance(this).todoDao().addTodo(newTodo)
        }
    }

    override fun todoUpdate(editedTodo: Todo) {
        // update the todo in the Database
        thread {
            AppDatabase.getInstance(this).todoDao().updateTodo(editedTodo)
        }
    }

}