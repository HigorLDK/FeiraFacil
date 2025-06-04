package com.higorapp.feirafacil.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.higorapp.feirafacil.Constants
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class AddItemViewModel(
    private val repository: FirestoreRepository,
    private val usuarioUID: String
) : ViewModel() {

    private val _resultado = MutableLiveData<String>()
    //val resultado: LiveData<String> get() = _resultado

    fun adicionarItem(tituloFeira: String, produto: String, quantidade: Int, valor: BigDecimal, categoria: String) {
        // Arredonda o valor e calcula o total com precisão
        val valorArredondado = valor.setScale(2, RoundingMode.HALF_EVEN)
        val valorTotal = valorArredondado.multiply(BigDecimal(quantidade)).setScale(2, RoundingMode.HALF_EVEN)

        val dados = mapOf(
            "categoria" to categoria,
            "produto" to produto,
            "quantidade" to quantidade,
            "valor" to valorArredondado.toDouble(),      // Converte para Double para salvar
            "valorTotal" to valorTotal.toDouble(),       // Converte para Double para salvar
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