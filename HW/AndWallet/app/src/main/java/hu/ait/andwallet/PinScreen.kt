package hu.ait.andwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.andwallet.databinding.ActivityPinScreenBinding
import hu.ait.andwallet.databinding.ActivitySummaryBinding

class PinScreen : AppCompatActivity() {
    
    lateinit var binding: ActivityPinScreenBinding
    val PIN = "5738"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            if (binding.pinText.text.toString() == PIN){
                val intentDetails = Intent()

                intentDetails.setClass(this,
                    MainActivity::class.java)

                startActivity(intentDetails)
            }
            else
                binding.pinText.error = getString(R.string.pin_error)
        }
    }
}