package com.pk4us.declarationtable.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pk4us.declarationtable.R


open class BaseAdsFragment : Fragment(),InterAdsClose{
    lateinit var adView: AdView
    var interAdd: InterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadInterAd()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun initAds() {
        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadInterAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context as Activity, getString(R.string.ad_inter_id),adRequest,object: InterstitialAdLoadCallback(){
            override fun onAdLoaded(ad: InterstitialAd) {
                interAdd = ad
            }
        })
    }

    fun showInterAd(){
        if (interAdd!=null){
            interAdd?.fullScreenContentCallback = object: FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    onClose()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onClose()
                }
            }
            interAdd?.show(activity as Activity)
        }else{
            onClose()
        }
    }

    override fun onClose() {

    }
}