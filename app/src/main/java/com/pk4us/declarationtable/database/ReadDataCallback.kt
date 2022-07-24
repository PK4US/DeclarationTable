package com.pk4us.declarationtable.database

import com.pk4us.declarationtable.data.Ad

interface ReadDataCallback {
    fun readData(list:List<Ad>)
}