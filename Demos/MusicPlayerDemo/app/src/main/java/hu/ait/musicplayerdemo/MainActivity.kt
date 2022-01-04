package hu.ait.musicplayerdemo

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.musicplayerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            mediaPlayer = MediaPlayer.create(this, R.raw.leaves_from_the_vine)
            mediaPlayer?.setOnPreparedListener(this)
        }

        binding.btnStop.setOnClickListener {
            mediaPlayer?.stop()
        }
    }

    override fun onPause() {
        try {
            mediaPlayer?.stop()
        }catch (e: Exception) {
            e.printStackTrace()
        }

        super.onPause()
    }

    override fun onPrepared(p0: MediaPlayer?) {
        mediaPlayer?.start()
    }
}