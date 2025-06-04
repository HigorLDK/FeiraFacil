package com.higorapp.feirafacil.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.higorapp.feirafacil.model.Lista
import com.higorapp.feirafacil.repository.FirestoreRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.time.times

class NovaFeiraViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val usuarioUID: String,
    private val tituloFeira: String
) : ViewModel() {

    private val _produtos = MutableLiveData<List<Lista>>()
    val produtos: LiveData<List<Lista>> get() = _produtos

    private val _valorTotal = MutableLiveData<BigDecimal>()
    val valorTotal: LiveData<BigDecimal> get() = _valorTotal

    private val _exclusaoEstado = MutableLiveData<Result<Unit>>()
    val exclusaoEstado: LiveData<Result<Unit>> get() = _exclusaoEstado

    fun recuperarProdutos() {
        viewModelScope.launch {
            try {
                val resultado = firestoreRepository.getItensDaFeira(usuarioUID, tituloFeira)
                _produtos.value = resultado.getOrThrow()
                _valorTotal.value = _produtos.value?.sumOf { it.valorTotal } ?: BigDecimal.ZERO
            } catch (e: Exception) {
                _produtos.value = emptyList()
                _valorTotal.value = BigDecimal.ZERO
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

    fun incrementarQuantidade(tituloFeira: String, idProduto: String) {
        // Atualizar localmente antes de chamar o Firestore
        val produtosAtualizados = _produtos.value?.map { produto ->
            if (produto.idProduto == idProduto) {
                val novaQuantidade = produto.quantidade + 1
                val novoValorTotal = BigDecimal(novaQuantidade).multiply(produto.valor ?: BigDecimal.ZERO)  // Calculando o valor total
                produto.copy(quantidade = novaQuantidade, valorTotal = novoValorTotal)
            } else produto
        }

        // Atualize o valor total localmente
        _produtos.value = produtosAtualizados!!
        _valorTotal.value = produtosAtualizados?.sumOf { it.valorTotal } ?: BigDecimal.ZERO

        // Agora, atualize o Firestore em segundo plano
        viewModelScope.launch {
            try {
                firestoreRepository.incrementarQuantidade(usuarioUID, tituloFeira, idProduto)
                    .onFailure { Log.e("erro_produto", "Erro ao incrementar: ${it.message}") }
            } catch (e: Exception) {
                Log.e("erro_produto", "Erro ao incrementar no Firestore: ${e.message}")
            }
        }
    }

    fun decrementarQuantidade(tituloFeira: String, idProduto: String) {
        // Atualizar localmente antes de chamar o Firestore
        val produtosAtualizados = _produtos.value?.map { produto ->
            if (produto.idProduto == idProduto && produto.quantidade > 0) {
                val novaQuantidade = produto.quantidade - 1
                val novoValorTotal = BigDecimal(novaQuantidade).multiply(produto.valor ?: BigDecimal.ZERO)  // Calculando o valor total
                produto.copy(quantidade = novaQuantidade, valorTotal = novoValorTotal)
            } else produto
        }

        // Atualize o valor total localmente
        _produtos.value = produtosAtualizados!!
        _valorTotal.value = produtosAtualizados?.sumOf { it.valorTotal } ?: BigDecimal.ZERO

        // Agora, atualize o Firestore em segundo plano
        viewModelScope.launch {
            try {
                firestoreRepository.decrementarQuantidade(usuarioUID, tituloFeira, idProduto)
                    .onFailure { Log.e("erro_produto", "Erro ao decrementar: ${it.message}") }
            } catch (e: Exception) {
                Log.e("erro_produto", "Erro ao decrementar no Firestore: ${e.message}")
            }
        }
    }





    fun zerarValoresItens(usuarioUID: String, tituloFeira: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.zerarValoresItens(usuarioUID,tituloFeira)
                recuperarProdutos()
            }catch (e: Exception){
                Log.i("teste", "erro não zerou")
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