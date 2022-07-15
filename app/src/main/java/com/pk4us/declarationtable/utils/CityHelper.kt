package com.pk4us.declarationtable.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

object CityHelper {
    fun getAllCountries(context:Context):ArrayList<String>{
        val tempArray = ArrayList<String>()
        try {
            val inputStream : InputStream = context.assets.open("countriesToCities.json")
            val size:Int = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            val jsonFile = String(byteArray)
            val jsonObject = JSONObject(jsonFile)
            val countriesNames = jsonObject.names()
            if (countriesNames != null){
                for (n in 0 until countriesNames.length()){
                    tempArray.add(countriesNames.getString(n))
                }
            }


        }catch (e:IOException){

        }
        return tempArray
    }
}