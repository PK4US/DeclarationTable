package com.pk4us.declarationtable.model

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pk4us.declarationtable.MainActivity

class DbManager {
    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Ad,finishListener:FinishWorkListener){
        if (auth.uid!=null)db.child(ad.key?:"empty").child(auth.uid!!).child(AD_NODE).setValue(ad).addOnCompleteListener {
            val adFilter = AdFilter(ad.time,"${ad.category}_${ad.time}")
            db.child(ad.key?:"empty").child(FILTER_NODE).setValue(adFilter).addOnCompleteListener {
                finishListener.onFinish()
            }
        }
    }

    fun adViewed(ad:Ad){
        var counter = ad.viewsCounter.toInt()
        counter++
        if (auth.uid!=null)db.child(ad.key?:"empty").child(INFO_NODE).setValue(InfoItem(counter.toString(),ad.emailCounter,ad.callsCounter))
    }

    fun onFavClick(ad: Ad, listener: FinishWorkListener){
        if (ad.isFav){
            removeFromFavs(ad,listener)
        }else{
            addToFavs(ad,listener)
        }
    }

    private fun addToFavs(ad: Ad, listener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVS_MODE).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful) listener.onFinish()
                }
            }
        }
    }

    private fun removeFromFavs(ad: Ad, listener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVS_MODE).child(uid).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) listener.onFinish()
                }
            }
        }
    }

    fun getMyAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid+"/ad/uid").equalTo(auth.uid)
        readDataFromDb(query,readDataCallback)
    }

    fun getMyFavs(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/favs/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query,readDataCallback)
    }

    fun deleteAd(ad: Ad, listener: FinishWorkListener) {
        if (ad.key == null || ad.uid == null) return
        db.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) listener.onFinish()
        }
    }

    fun getAllAds(lastTime:String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/time").startAfter(lastTime).limitToFirst(ADS_LIMIT)
        readDataFromDb(query,readDataCallback)
    }

    fun getAllAdsFromCat(lastCatTime:String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/catTime").startAfter(lastCatTime).limitToFirst(ADS_LIMIT)
        readDataFromDb(query,readDataCallback)
    }

    private fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?){
        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<Ad>()
                for (item in snapshot.children){
                    var ad:Ad? = null
                    item.children.forEach {
                        if (ad == null) ad = it.child(AD_NODE).getValue(Ad::class.java)
                    }
                    val infoItem = item.child(INFO_NODE).getValue(InfoItem::class.java)

                    val favCounter = item.child(FAVS_MODE).childrenCount
                    val isFav = auth.uid?.let { item.child(FAVS_MODE).child(it).getValue(String::class.java) }
                    ad?.isFav = isFav !=null
                    ad?.favCounter = favCounter.toString()

                    ad?.viewsCounter = infoItem?.viewCounter ?: "0"
                    ad?.emailCounter = infoItem?.emailCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"
                    if (ad!=null)adArray.add(ad!!)
                }
                readDataCallback?.readData(adArray)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    interface ReadDataCallback {
        fun readData(list:ArrayList<Ad>)
    }

    interface FinishWorkListener{
        fun onFinish()
    }

    companion object{
        const val AD_NODE = "ad"
        const val FILTER_NODE = "adFilter"
        const val MAIN_NODE = "main"
        const val INFO_NODE = "info"
        const val FAVS_MODE = "favs"
        const val ADS_LIMIT = 2
    }
}