package com.pk4us.declarationtable.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
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
    private var ad:Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fbTel.setOnClickListener {
            call()
        }
        binding.fbEmail.setOnClickListener {
            sendEmail()
        }
    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
    }


    private fun getIntentFromMainAct(){
        ad = intent.getSerializableExtra("AD") as Ad
        if (ad!=null) updateUi(ad!!)
    }

    private fun updateUi(ad: Ad){
        fillImageArray(ad)
        fillTextViews(ad)
    }

    private fun fillTextViews (ad: Ad) = with(binding){
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvEmail.text = ad.email
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

    private fun call(){
        val callUri = "tel:${ad?.tel}"
        val iCall = Intent  (Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }

    private fun sendEmail(){
        val iSendEmail = Intent(Intent.ACTION_SEND)
        iSendEmail.type = "message/rfc822"
        iSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT,"Объявление")
            putExtra(Intent.EXTRA_TEXT,"Меня интересует ваше обьявление!")
        }
        try {
            startActivity(Intent.createChooser(iSendEmail,"Открыть с "))
        }catch (e:ActivityNotFoundException){

        }
    }
}