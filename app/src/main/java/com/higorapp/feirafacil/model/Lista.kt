package com.higorapp.feirafacil.model

import java.io.Serializable
import java.math.BigDecimal

data class Lista(
    val categoria: String = "",
    val produto: String = "",
    val quantidade : Int = 0,
    val valor: BigDecimal = BigDecimal.ZERO,
    val valorTotal: BigDecimal = BigDecimal.ZERO,
    val idFeira : String = "",
    var idProduto : String = ""
) : Serializable
