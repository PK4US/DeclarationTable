package com.pk4us.declarationtable.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

class BillingManager(val act:AppCompatActivity) {
    private var billingClient:BillingClient? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient(){
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener()).enablePendingPurchases().build()
    }

    private fun savePurchase(isPurchase: Boolean){
        val pref = act.getSharedPreferences(MAIN_PREF,Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_PREF,isPurchase)
        editor.apply()
    }

    fun startConnection(){
        billingClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(p0: BillingResult) { getItem() }
        })
    }

    private fun getItem() {
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_ADS)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(skuDetails.build()) { result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (!list.isNullOrEmpty()){
                        val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(list[0]).build()
                        billingClient?.launchBillingFlow(act,billingFlowParams)
                    }
                }
            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase){
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
            if (purchase.isAcknowledged){
                val acParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acParams){result->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK){
                        savePurchase(true)
                        Toast.makeText(act,"Реклама отключена!",Toast.LENGTH_SHORT).show()
                    }else{
                        savePurchase(false)
                        Toast.makeText(act,"Ошибка! Не удалось реализовать покупку!",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPurchaseListener():PurchasesUpdatedListener{
        return PurchasesUpdatedListener {
                result, list ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK){
                    list?.get(0)?.let {nonConsumableItem(it)  }
            }
        }
    }

    fun closeConnection(){
        billingClient?.endConnection()
    }

    companion object{
        const val REMOVE_ADS_PREF = "remove_ads_pref"
        const val REMOVE_ADS = "remove_ads"
        const val MAIN_PREF = "main_pref"
    }
}