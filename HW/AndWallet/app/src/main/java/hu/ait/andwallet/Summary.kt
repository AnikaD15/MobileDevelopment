package hu.ait.andwallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.System.getString
import android.view.Menu
import android.view.MenuItem
import hu.ait.andwallet.MainActivity.Companion.balance
import hu.ait.andwallet.MainActivity.Companion.totalExpense
import hu.ait.andwallet.MainActivity.Companion.totalIncome
import hu.ait.andwallet.databinding.ActivityMainBinding
import hu.ait.andwallet.databinding.ActivitySummaryBinding

class Summary : AppCompatActivity() {

    lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvIncome.text = getString(R.string.income, totalIncome)
        binding.tvExpense.text = getString(R.string.expense, totalExpense)
        binding.tvBal.text = getString(R.string.balance, balance)
    }
}