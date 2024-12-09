package com.higorapp.feirafacil.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.higorapp.feirafacil.model.Feira
import com.higorapp.feirafacil.repository.FirestoreRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListaFeiraViewModel(
    private val repository: FirestoreRepository,
    private val usuarioUID: String
) : ViewModel() {

    private val _feiras = MutableLiveData<List<Feira>>()
    val feiras: LiveData<List<Feira>> get() = _feiras

    private val _exclusaoEstado = MutableLiveData<Result<Unit>>()
    val exclusaoEstado: LiveData<Result<Unit>> get() = _exclusaoEstado

    fun recuperarFeiras() {
        repository.recuperarFeiras(usuarioUID)
            .addSnapshotListener { querySnapshot, error ->
                val listaFeiras = mutableListOf<Feira>()

                querySnapshot?.documents?.forEach { documentSnapshot ->
                    val feira = documentSnapshot.toObject(Feira::class.java)
                    feira?.idFeira = documentSnapshot.id
                    feira?.data = documentSnapshot.getTimestamp("timestamp")?.toDate()
                    feira?.let { listaFeiras.add(it) }
                }
                _feiras.postValue(listaFeiras)
            }
    }

    fun excluirFeira(usuarioUID: String, idFeira: String, nomeFeira: String) {
        viewModelScope.launch {
            try {
                repository.excluirFeiraCompleta(usuarioUID, idFeira, nomeFeira).await()
                _exclusaoEstado.value = Result.success(Unit)
            } catch (e: Exception) {
                _exclusaoEstado.value = Result.failure(e)
            }
        }
    }

    class ListaFeiraViewModelFactory(
        private val repository: FirestoreRepository,
        private val usuarioUID: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListaFeiraViewModel::class.java)) {
                return ListaFeiraViewModel(repository, usuarioUID) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}