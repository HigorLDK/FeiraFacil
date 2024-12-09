package com.higorapp.feirafacil.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import java.util.Date

@Parcelize
data class Feira(
    var idFeira : String = "",
    var nomeFeira : String = "",
    var data: Date? = null // Data como objeto Date
) : Parcelable
