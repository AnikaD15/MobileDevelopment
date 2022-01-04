package hu.ait.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import hu.ait.stopwatch.databinding.ActivityMainBinding
import java.sql.Time

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var isTimerRunning = false
    var startTime : Long = 0
    var stopTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            if(!isTimerRunning){
                isTimerRunning = true
                binding.timer.base = SystemClock.elapsedRealtime()
                binding.timer.start()
                startTime = System.currentTimeMillis()

                // reset scroll view
                binding.timeContent.removeAllViewsInLayout()
            }
        }

        binding.btnStop.setOnClickListener {
            if(isTimerRunning){
                isTimerRunning = false
                binding.timer.stop()
                stopTime = System.currentTimeMillis()
            }
        }

        binding.btnMark.setOnClickListener {
            if(isTimerRunning){
                var current = System.currentTimeMillis()
                var timeDiff = current - startTime
                startTime = current
                addTimeItem(timeDiff)
            }
            else{
                var timeDiff = stopTime - startTime
                addTimeItem(timeDiff)
            }
        }
    }

    private fun addTimeItem(timeDiff:Long) {
        val layoutDetails = layoutInflater.inflate(R.layout.time_item, null)
        layoutDetails.findViewById<TextView>(R.id.timeText).text = "Lap time: ${timeDiff/1000} s"
        binding.timeContent.addView(layoutDetails)
    }
}