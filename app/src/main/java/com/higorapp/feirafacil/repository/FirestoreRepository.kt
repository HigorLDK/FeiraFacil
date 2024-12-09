package com.higorapp.feirafacil.repository

import android.util.Log
import com.higorapp.feirafacil.model.Lista
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val firebaseStore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun verificarEAdicionarFeira(usuarioUID: String, nomeFeira: String): Result<Unit> {
        return try {
            // Verifica se já existe uma feira com o mesmo nome
            val querySnapshot = firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("idFeiras")
                .whereEqualTo("nomeFeira", nomeFeira)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                // Não existe feira com esse nome, prossegue com a criação
                val dados = mapOf(
                    "nomeFeira" to nomeFeira,
                    "timestamp" to FieldValue.serverTimestamp()
                )

                firebaseStore.collection("usuarios")
                    .document(usuarioUID)
                    .collection("idFeiras")
                    .add(dados)
                    //.await()

                Result.success(Unit)
            } else {
                // Feira com o mesmo nome já existe
                Result.failure(Exception("Uma feira com esse título já existe!"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItensDaFeira(usuarioUID: String, tituloFeira: String): Result<List<Lista>> {
        return try {
            val querySnapshot = firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("feiras")
                .document(tituloFeira)
                .collection("itens")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            val produtos = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Lista::class.java)?.apply { idProduto = doc.id }
            }
            Result.success(produtos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun excluirItem(usuarioUID: String, tituloFeira: String, documentId: String): Result<Unit> {
        return try {
            firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("feiras")
                .document(tituloFeira)
                .collection("itens")
                .document(documentId)
                .delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun adicionarProduto(usuarioUID: String, tituloFeira: String, dados: Map<String, Any>) : Result<Unit>{
        return try {
        firebaseStore.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(tituloFeira)
            .collection("itens")
            .add(dados)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun recuperarProduto(usuarioUID: String, tituloFeira: String, idProduto: String) =
        firebaseStore.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(tituloFeira)
            .collection("itens")
            .document(idProduto)
            .get()

    fun atualizarProduto(usuarioUID: String, tituloFeira: String, idProduto: String, dados: Map<String, Any>) : Result<Unit> {
        return try {
        firebaseStore.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(tituloFeira)
            .collection("itens")
            .document(idProduto)
            .update(dados)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun recuperarFeiras(usuarioUID: String) =
        firebaseStore.collection("usuarios")
            .document(usuarioUID)
            .collection("idFeiras")
            .orderBy("timestamp", Query.Direction.DESCENDING)

    fun excluirFeiraCompleta(usuarioUID: String, idFeira: String, nomeFeira: String): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        // Referência à coleção de itens da feira
        val itensCollectionRef = db.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(nomeFeira)
            .collection("itens")

        // Referência ao documento da feira
        val feiraRef = db.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(nomeFeira)

        // Referência ao ID da feira
        val idFeiraRef = db.collection("usuarios")
            .document(usuarioUID)
            .collection("idFeiras")
            .document(idFeira)
        Log.d("ExcluirFeira", "Referência do usuário: usuarios/$usuarioUID")
        Log.d("ExcluirFeira", "Referência da feira: feiras/$nomeFeira")
        Log.d("ExcluirFeira", "Referência dos itens: usuarios/$usuarioUID/feiras/$nomeFeira/itens")
        return itensCollectionRef.get().continueWithTask { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                Log.d("ExcluirFeira", "Itens encontrados: ${querySnapshot?.size()}")
                querySnapshot?.forEach { itemDoc ->
                    Log.d("ExcluirFeira", "Excluindo item: ${itemDoc.id}")
                    batch.delete(itemDoc.reference) // Adiciona cada item ao batch
                }
                // Adiciona a exclusão da feira e do ID ao batch
                batch.delete(feiraRef)
                batch.delete(idFeiraRef)
                batch.commit() // Comita o batch
            } else {
                throw task.exception ?: Exception("Erro ao obter itens da feira")
            }
        }
    }

}