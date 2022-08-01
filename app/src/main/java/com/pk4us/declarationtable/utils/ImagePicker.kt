package com.pk4us.declarationtable.utils

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.act.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3

    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "/pix/images"
        }
        return options
    }

     fun addImages(editAdsAct: EditAdsAct, imageCount: Int) {
         editAdsAct.addPixToActivity(R.id.place_holder, getOptions(imageCount)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFragment(editAdsAct,)
                    editAdsAct.chooseImageFragment?.updateAdapter(result.data as ArrayList<Uri>,editAdsAct)

                }
                else -> {}
            }
        }
    }

    fun getMultiImages(editAdsAct: EditAdsAct, imageCount: Int) {
        editAdsAct.addPixToActivity(R.id.place_holder, getOptions(imageCount)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectImages(editAdsAct, result.data)
                    closePixFragment(editAdsAct)
                }
                else -> {}
            }
        }
    }

    fun getSingleImages(editAdsAct: EditAdsAct) {
        editAdsAct.addPixToActivity(R.id.place_holder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFragment(editAdsAct)
                    singleImage(editAdsAct,result.data[0])
                }
                else -> {}
            }
        }
    }

    private fun openChooseImageFragment (editAdsAct: EditAdsAct){
        editAdsAct.supportFragmentManager.beginTransaction().replace(R.id.place_holder,editAdsAct.chooseImageFragment!!).commit()
    }

    private fun closePixFragment(editAdsAct: EditAdsAct) {
        val fList = editAdsAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) editAdsAct.supportFragmentManager.beginTransaction().remove(it)
                .commit()
        }
    }

    fun getMultiSelectImages(editAdsAct: EditAdsAct, uris: List<Uri>) {
        if (uris.size > 1 && editAdsAct.chooseImageFragment == null) {
            editAdsAct.openChooseImageFragment(uris as ArrayList<Uri>)
        }  else if (uris.size == 1 && editAdsAct.chooseImageFragment == null) {
            CoroutineScope(Dispatchers.Main).launch {
                editAdsAct.binding.pBarLoad.visibility = View.VISIBLE
                val bitmapArray =
                    ImageManager.imageResize(uris, editAdsAct) as ArrayList<Bitmap>
                editAdsAct.binding.pBarLoad.visibility = View.GONE
                editAdsAct.imageAdapter.update(bitmapArray)
                closePixFragment(editAdsAct)
            }
        }
    }

    private fun singleImage(edAct: EditAdsAct, uri: Uri) {
        edAct.chooseImageFragment?.setSingleImage(uri, edAct.editImagePosition)

    }
}