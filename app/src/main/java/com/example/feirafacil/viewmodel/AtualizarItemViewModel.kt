package com.example.feirafacil.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feirafacil.Constants
import com.example.feirafacil.model.Lista
import com.example.feirafacil.repository.FirestoreRepository

class AtualizarItemViewModel(
    private val repository: FirestoreRepository,
    private val usuarioUID: String
) : ViewModel() {

    private val _produto = MutableLiveData<Lista?>()
    val produto: LiveData<Lista?> get() = _produto

    private val _atualizacaoEstado = MutableLiveData<String>()
    val atualizacaoEstado: LiveData<String> get() = _atualizacaoEstado

    fun recuperarProduto(tituloFeira: String, idProduto: String) {
        repository.recuperarProduto(usuarioUID, tituloFeira, idProduto)
            .addOnSuccessListener { document ->
                _produto.postValue(document.toObject(Lista::class.java))
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