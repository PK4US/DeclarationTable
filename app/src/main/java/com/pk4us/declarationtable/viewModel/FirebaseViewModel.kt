package com.pk4us.declarationtable.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pk4us.declarationtable.model.Ad
import com.pk4us.declarationtable.model.DbManager

class FirebaseViewModel:ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()
    fun loadAllAds(){
        dbManager.getAllAds(object :DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
             liveAdsData.value = list
            }
        })
    }

    fun adViewed(ad: Ad){
        dbManager.adViewed(ad)
    }

    fun loadMyAds(){
        dbManager.getMyAds(object :DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun deleteItem(ad: Ad){
        dbManager.deleteAd(ad,object :DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatesList = liveAdsData.value
                updatesList?.remove(ad)
                liveAdsData.postValue(updatesList)
            }
        })
    }
}