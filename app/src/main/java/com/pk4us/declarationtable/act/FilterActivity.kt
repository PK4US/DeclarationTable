package com.pk4us.declarationtable.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.databinding.ActivityFilterBinding
import com.pk4us.declarationtable.dialogs.DialogSpinnerHelper
import com.pk4us.declarationtable.utils.CityHelper

class FilterActivity : AppCompatActivity() {
    lateinit var binding:ActivityFilterBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickSelectCountry()
        onClickSelectCity()
        onClickDone()

        actionBarSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun onClickSelectCountry() = with(binding) {
        tvCountry.setOnClickListener{
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, tvCountry)
            if (tvCity.text.toString() != getString(R.string.select_city)) {
                tvCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding) {
        tvCity.setOnClickListener{
            val selectedCountry = tvCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCity = CityHelper.getAllCities(selectedCountry, this@FilterActivity)
                dialog.showSpinnerDialog(this@FilterActivity, listCity, tvCity)
            } else {
                Toast.makeText(this@FilterActivity, "Выберете страну", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onClickDone() = with(binding) {
        btDone.setOnClickListener{
            Log.d("MyLog","Filter : ${createFilter()}")
        }
    }

    private fun createFilter():String = with(binding){
        val sBuilder = StringBuilder()
        val arrayTempFilter = listOf(tvCountry.text,
            tvCity.text,
            editIndex.text,
            checkBoxWithSend.isChecked.toString())
        for ((i,s) in arrayTempFilter.withIndex()){
            if (s!=getString(R.string.select_country)&& s!=getString(R.string.select_city) && s.isNotEmpty()){
                sBuilder.append(s)
                if (i!=arrayTempFilter.size - 1)sBuilder.append("_")
            }
        }
        return sBuilder.toString()
    }

    fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}