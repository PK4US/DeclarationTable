package com.pk4us.declarationtable.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pk4us.declarationtable.databinding.ActivityEditAdsBinding
import com.pk4us.declarationtable.dialogs.DialogSpinnerHelper
import com.pk4us.declarationtable.utils.CityHelper

class EditAdsAct : AppCompatActivity() {

    lateinit var binding:ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){

    }

    //onClicks
    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this,listCountry)
    }
}