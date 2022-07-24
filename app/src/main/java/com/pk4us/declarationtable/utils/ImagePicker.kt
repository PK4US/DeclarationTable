package com.pk4us.declarationtable.utils

import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.pk4us.declarationtable.act.EditAdsAct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGES = 998

    private fun getOptions(imageCounter: Int) : Options{
        val options = Options.init()
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")
        return options
    }

    fun launcher(editAdsAct: EditAdsAct, launcher: ActivityResultLauncher<Intent>?, imageCount:Int){
        PermUtil.checkForCamaraWritePermissions(editAdsAct){
            val intent = Intent(editAdsAct,Pix::class.java).apply {
                putExtra("options", getOptions(imageCount))
            }
            launcher?.launch(intent)
        }
    }

    fun getLauncherForMultiSelectImages(editAdsAct: EditAdsAct): ActivityResultLauncher<Intent> {
        return editAdsAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val returnValues = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    if (returnValues?.size!! > 1 && editAdsAct.chooseImageFragment == null) {
                        editAdsAct.openChooseImageFragment(returnValues)
                    } else if (editAdsAct.chooseImageFragment != null) {
                        editAdsAct.chooseImageFragment?.updateAdapter(returnValues)
                    } else if (returnValues.size == 1 && editAdsAct.chooseImageFragment == null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            editAdsAct.binding.pBarLoad.visibility = View.VISIBLE
                            val bitmapArray =
                                ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                            editAdsAct.binding.pBarLoad.visibility = View.GONE
                            editAdsAct.imageAdapter.update(bitmapArray)
                        }
                    }
                }
            }
        }
    }

    fun getLauncherForSingleImage(edAct: EditAdsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFragment?.setSingleImage(uris?.get(0)!!, edAct.editImagePosition
                    )
                }
            }
        }
    }
}