package com.higorapp.feirafacil.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.higorapp.feirafacil.repository.AuthRepository
import com.higorapp.feirafacil.repository.FirestoreRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _loginState = MutableLiveData<Result<String>>()
    val loginState: LiveData<Result<String>> get() = _loginState

    private val _statusFeira = MutableLiveData<Result<Unit>>()
    val statusFeira: LiveData<Result<Unit>> get() = _statusFeira

    fun logarAnonimo() {
        viewModelScope.launch {
            val result = authRepository.logarAnonimo()
            _loginState.value = result.map { it.uid }
        }
    }

    fun salvarIdFeira(usuarioUID: String, nomeFeira: String) {
        viewModelScope.launch {
            try {
                val resultado = firestoreRepository.verificarEAdicionarFeira(usuarioUID, nomeFeira)
                _statusFeira.value = resultado
            } catch (e: Exception) {
                _statusFeira.value = Result.failure(e)
            }
        }
    }

    //A factory foi feita dentro desse companion e é chamada de um jeito mais pratico na Activity
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            private val authRepo = AuthRepository()
            private val firestoreRepo = FirestoreRepository()

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    return MainViewModel(authRepo, firestoreRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}