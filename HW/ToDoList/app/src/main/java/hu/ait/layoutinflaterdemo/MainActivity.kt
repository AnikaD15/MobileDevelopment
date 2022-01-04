package hu.ait.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import hu.ait.todolist.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            addDetailsItem()
        }
    }

    private fun addDetailsItem() {
        val layoutDetails = layoutInflater.inflate(R.layout.details_item, null)

        try {
            var taskText = binding.edText.text
            layoutDetails.findViewById<TextView>(R.id.taskItem).text = taskText

            // binding only works for main activity
            layoutDetails.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                // root - general linear layout
                binding.layoutContent.removeView(layoutDetails)
            }

            binding.layoutContent.addView(layoutDetails)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
}