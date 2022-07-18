package com.pk4us.declarationtable.act

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.databinding.ActivityEditAdsBinding
import com.pk4us.declarationtable.dialogs.DialogSpinnerHelper
import com.pk4us.declarationtable.fragment.FragmentCloseInterface
import com.pk4us.declarationtable.fragment.ImageListFragment
import com.pk4us.declarationtable.utils.CityHelper
import com.pk4us.declarationtable.utils.ImagePicker

class EditAdsAct : AppCompatActivity(),FragmentCloseInterface {

    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this,3)
                } else {
                    Toast.makeText(this, "Approve permission ti open Pix ImagePecker", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun init() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)                             //______________________УСТАРЕЛА_______________
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES){
            if (data!=null){
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS) as ArrayList<String>
                if (returnValues.size>1){
                    binding.scrollViewMain.visibility = View.GONE
                    val fm = supportFragmentManager.beginTransaction()
                    fm.replace(R.id.place_holder,ImageListFragment(this,returnValues))
                    fm.commit()
                }
            }
        }
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

    fun onClickGetImage(view: View){
        ImagePicker.getImages(this,3)
    }

    override fun onFragClose() {
        binding.scrollViewMain.visibility = View.VISIBLE
    }
}