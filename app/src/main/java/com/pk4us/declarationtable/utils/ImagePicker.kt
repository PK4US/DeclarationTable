package com.pk4us.declarationtable.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.pk4us.declarationtable.act.EditAdsAct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGES = 998
    fun getImages(context:AppCompatActivity,imageCounter : Int, rCode:Int){
        val options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
        Pix.start(context,options)
    }

    fun showSelectedImages(resultCode:Int,requestCode: Int,data:Intent?,editAdsAct: EditAdsAct){
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValues?.size!! > 1 && editAdsAct.chooseImageFragment == null) {
                    editAdsAct.openChooseImageFragment(returnValues)
                }else if (editAdsAct.chooseImageFragment != null) {
                    editAdsAct.chooseImageFragment?.updateAdapter(returnValues)
                }else if (returnValues.size == 1 && editAdsAct.chooseImageFragment == null){
                    CoroutineScope(Dispatchers.Main).launch{
                        editAdsAct.binding.pBarLoad.visibility = View.VISIBLE
                        val bitmapArray = ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                        editAdsAct.binding.pBarLoad.visibility = View.GONE
                        editAdsAct.imageAdapter.update(bitmapArray)
                    }
                }
            }
        }else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_SINGLE_IMAGES){
            if (data != null) {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                editAdsAct.chooseImageFragment?.setSingleImage(uris?.get(0)!!,editAdsAct.editImagePosition)
            }
        }
    }
}