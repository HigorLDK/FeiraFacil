package com.example.feirafacil.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Feira(
    var idFeira : String = "",
    var nomeFeira : String = ""
) : Parcelable
