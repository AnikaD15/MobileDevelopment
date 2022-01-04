package hu.ait.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import hu.ait.shoppinglist.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val img_anim = AnimationUtils.loadAnimation(this, R.anim.img_anim)
        val text_anim = AnimationUtils.loadAnimation(this, R.anim.text_anim)

        img_anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                binding.tvTitle.startAnimation(text_anim)
            }

            override fun onAnimationEnd(p0: Animation?) {
                // launch new activity
                val intentDetails = Intent()
                intentDetails.setClass(this@SplashActivity,
                    ScrollingActivity::class.java)
                startActivity(intentDetails)
                finish()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        binding.imgIcon.startAnimation(img_anim)
    }
}