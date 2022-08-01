package com.pk4us.declarationtable.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.databinding.ActivityDescriptionBinding

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding:ActivityDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}