package com.higorapp.feirafacil.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    suspend fun logarAnonimo(): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuário retornado é nulo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUsuarioAtual(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

}