package com.pk4us.declarationtable.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.databinding.ActivityEditAdsBinding
import com.pk4us.declarationtable.databinding.ActivityMainBinding

class EditAdsAct : AppCompatActivity() {

    private lateinit var binding:ActivityEditAdsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root) 
    }
}