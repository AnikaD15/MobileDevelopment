package hu.ait.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import hu.ait.minesweeper.databinding.ActivityMainBinding
import hu.ait.minesweeper.view.MineSweeperView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener {
            binding.mineSweeperView.resetField()
        }
    }

    fun isFlagMode() : Boolean {
        return binding.tgbtnIsFlag.isChecked()
    }

    fun showMsg(msg: String){
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        // Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}