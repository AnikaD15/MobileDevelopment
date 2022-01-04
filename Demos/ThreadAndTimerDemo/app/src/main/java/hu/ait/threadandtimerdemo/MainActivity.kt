package hu.ait.threadandtimerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.threadandtimerdemo.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var enabled = false

    inner class MyThread : Thread() {
        // need to override run method
        // this what happen when thread runs
        override fun run() {
            while (enabled) {
                runOnUiThread {
                    binding.tvData.append("#")
                }

                sleep(1000)
            }
        }
    }

    inner class MyTimerTask : TimerTask() {
        override fun run() {
            // no need for loop
            runOnUiThread {
                binding.tvData.append("W")
            }
        }
    }

    var mainTimer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            // create object to start thread
            if (!enabled) {
                enabled = true
                MyThread().start()
            }

            // mainTimer = Timer()
            // one timer object can be made for multiple timer tasks
            mainTimer.schedule(MyTimerTask(), 3000, 1000)
        }

        binding.btnStop.setOnClickListener {
            enabled = false
            mainTimer.cancel()
        }
    }


    override fun onDestroy() {
        enabled = false
        try {
            mainTimer.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

}