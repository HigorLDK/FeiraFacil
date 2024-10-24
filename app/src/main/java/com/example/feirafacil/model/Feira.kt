package com.example.feirafacil.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Feira(
    val idFeira : String = ""
) : Parcelable
