package com.example.feirafacil.model

import java.io.Serializable

data class Lista(
    val produto: String = "",
    val quantidade : Int = 0,
    val valor: Double = 0.0,
    val valorTotal: Double = 0.0,
    val idFeira : String = "",
    var idProduto : String = ""
) : Serializable
