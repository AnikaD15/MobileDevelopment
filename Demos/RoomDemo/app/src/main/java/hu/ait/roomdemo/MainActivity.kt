package hu.ait.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.roomdemo.data.AppDatabase
import hu.ait.roomdemo.data.Grade
import hu.ait.roomdemo.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.btnSubmit.setOnClickListener { 
            saveGrade()
        }

        binding.btnSearch.setOnClickListener {
            queryGrade()
        }
    }

    private fun saveGrade() {
        thread{
            AppDatabase.getInstance(this).gradeDao().insertGrades(
                Grade(null,
                binding.etStudentId.text.toString(),
                binding.etGrade.text.toString())
            )
        }
    }

    private fun queryGrade(){
        thread {
            val grades = AppDatabase.getInstance(this).gradeDao().getAllGrades()

            runOnUiThread{
                binding.tvResult.text = ""
                grades.forEach {
                    binding.tvResult.append("${it.studentId} - ${it.grade}\n")
                }
            }
        }
    }
}