package com.example.feirafacil.model

import java.io.Serializable

data class Lista(
    val idProduto : Int, val itemProduto: String, val quantProduto : Int, val precoProduto: Double,val idFeiraMae : Long
) : Serializable
