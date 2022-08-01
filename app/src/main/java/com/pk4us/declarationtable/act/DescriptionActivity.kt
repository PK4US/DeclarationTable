package com.pk4us.declarationtable.act

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.adapters.ImageAdapter
import com.pk4us.declarationtable.databinding.ActivityDescriptionBinding
import com.pk4us.declarationtable.model.Ad
import com.pk4us.declarationtable.utils.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding:ActivityDescriptionBinding
    lateinit var adapter:ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init() }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
    }


    private fun getIntentFromMainAct(){
        val ad = intent.getSerializableExtra("AD") as Ad
        updateUi(ad)
    }

    private fun updateUi(ad: Ad){
        fillImageArray(ad)
        fillTextViews(ad)
    }

    private fun fillTextViews (ad: Ad) = with(binding){
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvPrice.text = ad.price
        tvTel.text = ad.tel
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvIndex.text = ad.index
        tvWithSent.text = isWithSent(ad.withSent.toBoolean())
    }

    private fun isWithSent (withSent:Boolean):String{
        return if (withSent) "Да" else "Нет"
    }

    private fun fillImageArray (ad: Ad){
        val listUris = listOf(ad.mainImage,ad.image2,ad.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.getBitmapFromUris(listUris)
            adapter.update(bitmapList as ArrayList<Bitmap>)
        }
    }
}