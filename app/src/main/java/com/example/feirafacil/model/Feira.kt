package com.example.feirafacil.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date

@Parcelize
data class Feira(
    var idFeira : String = "",
    var nomeFeira : String = "",
    var data: Date? = null // Data como objeto Date
) : Parcelable
