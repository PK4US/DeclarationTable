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

    fun loadMyAds(){
        dbManager.getMyAds(object :DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }
}