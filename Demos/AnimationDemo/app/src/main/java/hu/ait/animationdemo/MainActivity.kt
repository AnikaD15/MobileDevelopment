package hu.ait.animationdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAnim = findViewById<Button>(R.id.btnAnimation)
        val anim = AnimationUtils.loadAnimation(this, R.anim.demo_anim)

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                TODO("Not yet implemented")
            }

            override fun onAnimationEnd(p0: Animation?) {
                // here for example we can start a new activity
                Toast.makeText(this@MainActivity,
                    "Animation over", Toast.LENGTH_LONG).show()
            }

            override fun onAnimationRepeat(p0: Animation?) {
                TODO("Not yet implemented")
            }
        })

        btnAnim.setOnClickListener {
            btnAnim.startAnimation(anim)
        }
    }
}