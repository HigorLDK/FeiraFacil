package com.higorapp.feirafacil.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.higorapp.feirafacil.model.Lista
import com.higorapp.feirafacil.repository.FirestoreRepository
import kotlinx.coroutines.launch

class NovaFeiraViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val usuarioUID: String,
    private val tituloFeira: String
) : ViewModel() {

    private val _produtos = MutableLiveData<List<Lista>>()
    val produtos: LiveData<List<Lista>> get() = _produtos

    private val _valorTotal = MutableLiveData<Double>()
    val valorTotal: LiveData<Double> get() = _valorTotal

    private val _exclusaoEstado = MutableLiveData<Result<Unit>>()
    val exclusaoEstado: LiveData<Result<Unit>> get() = _exclusaoEstado

    fun recuperarProdutos() {
        viewModelScope.launch {
            try {
                val resultado = firestoreRepository.getItensDaFeira(usuarioUID, tituloFeira)
                _produtos.value = resultado.getOrThrow()
                _valorTotal.value = _produtos.value?.sumOf { it.valorTotal } ?: 0.0
            } catch (e: Exception) {
                _produtos.value = emptyList()
                _valorTotal.value = 0.0
            }
        }
    }

    fun excluirProduto(documentId: String) {
        viewModelScope.launch {
            try {
                // Excluir o documento no Firestore
               firestoreRepository.excluirItem(usuarioUID, tituloFeira, documentId)
               recuperarProdutos()
            } catch (e: Exception) {
                _exclusaoEstado.value = Result.failure(e)
            }
        }
    }

    companion object {
        fun provideFactory(
            firestoreRepository: FirestoreRepository,
            usuarioUID: String,
            tituloFeira: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(NovaFeiraViewModel::class.java)) {
                        return NovaFeiraViewModel(firestoreRepository, usuarioUID, tituloFeira) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}