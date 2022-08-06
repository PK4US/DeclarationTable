package com.pk4us.declarationtable.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.pk4us.declarationtable.MainActivity
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.adapters.ImageAdapter
import com.pk4us.declarationtable.model.Ad
import com.pk4us.declarationtable.model.DbManager
import com.pk4us.declarationtable.databinding.ActivityEditAdsBinding
import com.pk4us.declarationtable.dialogs.DialogSpinnerHelper
import com.pk4us.declarationtable.fragment.FragmentCloseInterface
import com.pk4us.declarationtable.fragment.ImageListFragment
import com.pk4us.declarationtable.utils.CityHelper
import com.pk4us.declarationtable.utils.ImageManager
import com.pk4us.declarationtable.utils.ImagePicker
import java.io.ByteArrayOutputStream

class EditAdsAct : AppCompatActivity(),FragmentCloseInterface {
    var chooseImageFragment : ImageListFragment? = null
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    lateinit var adapter:ImageAdapter
    val dbManager = DbManager()
    var editImagePosition = 0
    private var imageIndex = 0
    private var isEditState = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        checkEditState()
        imageChangeCounter()
    }

    private fun checkEditState(){
        isEditState = isEditState()
        if (isEditState()){
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA)as Ad
            if (ad !=null) fillViews(ad!!)
        }
    }

    private fun isEditState():Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE,false)
    }

    private fun fillViews(ad:Ad)= with(binding){
        tvCounty.text = ad.country
        tvCity.text = ad.city
        editTel.setText(ad.tel)
        editIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSent.toBoolean()
        tvCategory.text = ad.category
        editTitle.setText(ad.title)
        etPrice.setText(ad.price)
        etDescription.setText(ad.description)

        ImageManager.fillImageArray(ad,adapter)
    }

    private fun init() {
        adapter = ImageAdapter()
        binding.vpImages.adapter = adapter
    }

    //onClicks
    fun onClickSelectCountry(view: View) {
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCounty)
        if (binding.tvCity.text.toString() != getString(R.string.select_city)) {
            binding.tvCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = binding.tvCounty.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this, "Выберете страну", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickSelectCategory(view: View) {
        val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this,listCategory,binding.tvCategory)

    }

    fun onClickGetImages(view: View){
        if (adapter.mainArray.size == 0){
            ImagePicker.getMultiImages(this,3)
        } else{
            openChooseImageFragment(null)
            chooseImageFragment?.updateAdapterFromEdit(adapter.mainArray)
        }
    }

    fun onClickPublish(view: View){
        ad = fillAd()
        if (isEditState){
             dbManager.publishAd(ad!!,onPublishFinish())
        }else{
//            dbManager.publishAd(adTemp,onPublishFinish())
            uploadImages()
        }
    }

    private fun onPublishFinish():DbManager.FinishWorkListener{
        return object :DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillAd():Ad{
        val adTemp:Ad
        binding.apply {
            adTemp = Ad(
                tvCounty.text.toString(),
                tvCity.text.toString(),
                editTel.text.toString(),
                editIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                editTitle.text.toString(),
                tvCategory.text.toString(),
                etPrice.text.toString(),
                etDescription.text.toString(),
                edEmail.text.toString(),
                ad?.mainImage ?: "empty",
                ad?.image2 ?: "empty",
                ad?.image3 ?: "empty",
                ad?.key ?: dbManager.db.push().key,
                "0",
                dbManager.auth.uid,
                ad?.image3 ?: System.currentTimeMillis().toString())
        }
        return adTemp
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        adapter.update(list)
        chooseImageFragment = null
    }

    fun openChooseImageFragment(newList:ArrayList<Uri>?){
        chooseImageFragment = ImageListFragment(this)
        if (newList!= null) chooseImageFragment?.resizeSelectedImages(newList,true,this)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()
    }

    private fun uploadImages(){
        if (adapter.mainArray.size == imageIndex){
            dbManager.publishAd(ad!!,onPublishFinish())
            return
        }
        val byteArray = prepareImageByteArray(adapter.mainArray[imageIndex])
        uploadImage(byteArray){
            //dbManager.publishAd(ad!!,onPublishFinish())
            nextImage(it.result.toString())
        }
    }

    private fun setImageUriToAd(uri: String){
        when (imageIndex){
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }

    private fun nextImage(uri:String){
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun prepareImageByteArray(bitmap: Bitmap):ByteArray{
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,20, outStream)
        return outStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray,listener:OnCompleteListener<Uri>){
        val imStorageRef = dbManager.dbStorage.child(dbManager.auth.uid!!).child("image_${System.currentTimeMillis()}")
        val upTask = imStorageRef.putBytes(byteArray)
        upTask.continueWithTask{
            task -> imStorageRef.downloadUrl
        }.addOnCompleteListener (listener)
    }

    private fun imageChangeCounter(){
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position +1}/${binding.vpImages.adapter?.itemCount}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }
}