package hu.ait.andwallet

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import hu.ait.andwallet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    companion object {
        var totalIncome: Float = 0F
        var totalExpense: Float = 0F
        var balance: Float = 0F
    }

    private var itemAmt = 0F
    private lateinit var itemName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvBalance.text = getString(R.string.balance, 0F)

        binding.btnSave.setOnClickListener {
            if (binding.etTitle.text.isEmpty()) {
                binding.etTitle.error = "Enter expense/income name"
            } else if (binding.etAmnt.text.isEmpty()) {
                binding.etAmnt.error = "Enter expense/income amount"
            } else if (binding.etAmnt.text.toString().toFloat() < 0) {
                binding.etAmnt.error = "Enter amount >= 0"
            } else {
                // update income/expense total
                try {
                    itemAmt = binding.etAmnt.text.toString().toFloat()
                    if (isExpense())
                        totalExpense += itemAmt
                    else
                        totalIncome += itemAmt

                    // add item to list on screen
                    addItem()

                    // update balance
                    updateBalance()
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.etAmnt.error = "Amount is invalid."
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.summary) {
            val intentDetails = Intent()

            intentDetails.setClass(
                this,
                Summary::class.java
            )

            startActivity(intentDetails)

        } else if (item.itemId == R.id.delete_all) {
            binding.listLayout.removeAllViews()
            totalExpense = 0F
            totalIncome = 0F
            updateBalance()
        }

        return true
    }

    // adds item to list
    fun addItem() {
        val layoutDetails = layoutInflater.inflate(R.layout.item_details, null)

        itemName = binding.etTitle.text.toString()
        layoutDetails.findViewById<TextView>(R.id.tvTitle).text = itemName
        layoutDetails.findViewById<TextView>(R.id.tvAmnt).text =
            getString(R.string.amnt_list, itemAmt)

        if(isExpense())
            layoutDetails.findViewById<ImageView>(R.id.imgIcon).setImageResource(R.drawable.price_tag)

        layoutDetails.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            binding.listLayout.removeView(layoutDetails)
        }

        binding.listLayout.addView(layoutDetails)
    }

    // checks toggle button
    fun isExpense(): Boolean {
        return binding.btnToggle.isChecked()
    }

    // updates balance text view
    fun updateBalance() {
        balance = totalIncome - totalExpense
        binding.tvBalance.text = getString(R.string.balance, balance)

        if (balance > 0)
            binding.tvBalance.setTextColor(Color.GREEN)
        else
            binding.tvBalance.setTextColor(Color.RED)
    }
}