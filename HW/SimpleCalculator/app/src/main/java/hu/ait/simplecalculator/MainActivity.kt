// Anika Duffus
// optional HW #1

package hu.ait.simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import hu.ait.simplecalculator.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlus.setOnClickListener {
            Log.d("MainActivity", "btnPlus was pressed")

            try {
                if (binding.num1.text.isNotEmpty() && binding.num2.text.isNotEmpty()) {
                    var num1 = binding.num1.text.toString().toInt()

                    try{
                        var num2 = binding.num2.text.toString().toInt()
                        var sum = num1+num2

                        binding.tvResult.text = "Result: $sum"
                    }

                    catch (e: Exception) {
                        e.printStackTrace()
                        binding.num2.error = "Number is too large or small!"
                    }

                }
            }  catch (e: Exception) {
                e.printStackTrace()
                binding.num1.error = "Number is too large or small!"
            }
        }

        binding.btnMinus.setOnClickListener {
            Log.d("MainActivity", "btnMinus was pressed")

            try {
                if (binding.num1.text.isNotEmpty() && binding.num2.text.isNotEmpty()) {
                    var num1 = binding.num1.text.toString().toInt()

                    try{
                        var num2 = binding.num2.text.toString().toInt()
                        var sum = num1-num2

                        binding.tvResult.text = "Result: $sum"
                    }

                    catch (e: Exception) {
                        e.printStackTrace()
                        binding.num2.error = "Number is too large or small!"
                    }

                }
            }  catch (e: Exception) {
                e.printStackTrace()
                binding.num1.error = "Number is too large or small!"
            }
        }
    }
}

