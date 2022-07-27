package com.pk4us.declarationtable.act

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.fxn.utility.PermUtil
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
import com.pk4us.declarationtable.utils.ImagePicker

class EditAdsAct : AppCompatActivity(),FragmentCloseInterface {
    var chooseImageFragment : ImageListFragment? = null
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter:ImageAdapter
    val dbManager = DbManager()
    var launcherMultiSelectImage:ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage:ActivityResultLauncher<Intent>? = null
    var editImagePosition = 0
    private var isEditState = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        checkEditState()
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
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvCategory.text = ad.category
        editTitle.setText(ad.title)
        etPrice.setText(ad.price)
        etDescription.setText(ad.description)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // ImagePicker.getImages(this,3,ImagePicker.REQUEST_CODE_GET_IMAGES )
                } else {
                    Toast.makeText(this, "Approve permission ti open Pix ImagePecker", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter

        launcherMultiSelectImage = ImagePicker.getLauncherForMultiSelectImages(this)
        launcherSingleSelectImage = ImagePicker.getLauncherForSingleImage(this)
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
        if (imageAdapter.mainArray.size == 0){
            ImagePicker.launcher(this,launcherMultiSelectImage,3)
        } else{
            openChooseImageFragment(null)
            chooseImageFragment?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
    }

    fun onClickPublish(view: View){
        val adTemp = fillAd()
        if (isEditState){
            dbManager.publishAd(adTemp.copy(key = ad?.key),onPublishFinish())
        }else{
            dbManager.publishAd(adTemp,onPublishFinish())
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
        val ad:Ad
        binding.apply {
            ad = Ad(
                tvCounty.text.toString(),
                tvCity.text.toString(),
                editTel.text.toString(),
                editIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                editTitle.text.toString(),
                tvCategory.text.toString(),
                etPrice.text.toString(),
                etDescription.text.toString(),
                dbManager.db.push().key,
                "0",
                dbManager.auth.uid)
        }
        return ad
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
    }

    fun openChooseImageFragment(newList:ArrayList<String>?){
        chooseImageFragment = ImageListFragment(this, newList)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()
    }
}