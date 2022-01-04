package hu.ait.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import hu.ait.tictactoe.databinding.ActivityMainBinding
import hu.ait.tictactoe.model.TicTacToeModel
import hu.ait.tictactoe.view.TicTacToeView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var running: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var timer = binding.timer
        timer.format = getString(R.string.time_format)

        binding.btnReset.setOnClickListener {
            binding.ticTacToeView.resetGame()
        }

    }

    // call function inside view class
    // text view is outside tictactoe view so it is not written there
    fun showTextMessage(msg: String) {
        binding.tvPlayer.text = msg
    }

    fun showMessage(msg: String) {
        // Snackbar.make(this, binding.ticTacToeView, msg, LENGTH_INDEFINITE)
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun startTimer() {
        if (!running) {
            running = true
            binding.timer.base = SystemClock.elapsedRealtime()
            binding.timer.start()
        }
    }

    fun restartTimer() {
        if (running) {
            binding.timer.base = SystemClock.elapsedRealtime()
            //running = false
            binding.timer.stop()
            binding.timer.start()
        }
    }

    fun stopTimer() {
        binding.timer.stop()
        running=false
    }

    fun resetTimer() {
        binding.timer.base = SystemClock.elapsedRealtime()
        binding.timer.stop()
        running=false
    }
}