package com.higorapp.feirafacil.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.higorapp.feirafacil.Constants
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.google.firebase.firestore.FieldValue

class AddItemViewModel(
    private val repository: FirestoreRepository,
    private val usuarioUID: String
) : ViewModel() {

    private val _resultado = MutableLiveData<String>()
    //val resultado: LiveData<String> get() = _resultado

    fun adicionarItem(tituloFeira: String, produto: String, quantidade: Int, valor: Double) {
        val valorTotal = quantidade * valor

        val dados = mapOf(
            "produto" to produto,
            "quantidade" to quantidade,
            "valor" to valor,
            "valorTotal" to valorTotal,
            "idFeira" to tituloFeira,
            "timestamp" to FieldValue.serverTimestamp()
        )

        val result = repository.adicionarProduto(usuarioUID, tituloFeira, dados)
        result.onSuccess {
            _resultado.value = Constants.SUCCESS
            Log.i("info_produto", "Sucesso ao adicionar produto")
        }.onFailure { erro ->
            _resultado.value = "Erro: ${erro.message ?: "Erro desconhecido"}"
        }


    }

    class AddItemViewModelFactory(
        private val repository: FirestoreRepository,
        private val usuarioUID: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
                return AddItemViewModel(repository, usuarioUID) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}