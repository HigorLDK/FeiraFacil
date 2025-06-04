package com.higorapp.feirafacil.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.higorapp.feirafacil.Constants
import com.higorapp.feirafacil.model.Lista
import com.higorapp.feirafacil.repository.FirestoreRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class AtualizarItemViewModel(
    private val repository: FirestoreRepository,
    private val usuarioUID: String,


) : ViewModel() {

    private val _produto = MutableLiveData<Lista?>()
    val produto: LiveData<Lista?> get() = _produto

    private val _atualizacaoEstado = MutableLiveData<String>()
    val atualizacaoEstado: LiveData<String> get() = _atualizacaoEstado

    fun recuperarProduto(tituloFeira: String, idProduto: String) {
        repository.recuperarProduto(usuarioUID, tituloFeira, idProduto)
            .addOnSuccessListener { document ->

                val valor = when (val v = document.get("valor")) {
                    is Number -> v.toDouble().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                    is String -> v.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
                    else -> BigDecimal.ZERO
                }

                val valorTotal = when (val vt = document.get("valorTotal")) {
                    is Number -> vt.toDouble().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                    is String -> vt.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
                    else -> BigDecimal.ZERO
                }

                val produto = try {
                    Lista(
                        idProduto = document.id,
                        produto = document.getString("produto") ?: "",
                        quantidade = (document.getLong("quantidade") ?: 0L).toInt(),
                        categoria = document.getString("categoria") ?: "",
                        valor = valor,
                        valorTotal = valorTotal
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                _produto.postValue(produto)
            }
            .addOnFailureListener {
                _produto.postValue(null)
            }
    }


    fun atualizarProduto(tituloFeira: String, idProduto: String, dados: Map<String, Any>) {
        val result = repository.atualizarProduto(usuarioUID, tituloFeira, idProduto, dados)
        result.onSuccess {
            _atualizacaoEstado.value = Constants.SUCCESS
        }.onFailure { erro ->
            _atualizacaoEstado.value = "Erro: ${erro.message ?: "Erro desconhecido"}"
        }
    }




    class AtualizarItemViewModelFactory(
        private val repository: FirestoreRepository,
        private val usuarioUID: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AtualizarItemViewModel::class.java)) {
                return AtualizarItemViewModel(repository, usuarioUID) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}