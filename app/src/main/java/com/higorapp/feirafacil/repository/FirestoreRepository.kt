package com.higorapp.feirafacil.repository

import com.higorapp.feirafacil.model.Lista
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.math.RoundingMode

class FirestoreRepository(private val firebaseStore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun verificarEAdicionarFeira(usuarioUID: String, nomeFeira: String): Result<Unit> {
        return try {
            val feiraRef = firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("idFeiras")

            val querySnapshot = try {
                feiraRef.whereEqualTo("nomeFeira", nomeFeira).get(Source.SERVER).await()
            } catch (e: Exception) {
                feiraRef.whereEqualTo("nomeFeira", nomeFeira).get(Source.CACHE).await()
            }

            if (querySnapshot.isEmpty) {
                val dados = mapOf(
                    "nomeFeira" to nomeFeira,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                feiraRef.add(dados)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Uma feira com esse título já existe!"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItensDaFeira(usuarioUID: String, tituloFeira: String): Result<List<Lista>> {
        return try {
            val itensRef = firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("feiras")
                .document(tituloFeira)
                .collection("itens")

            val querySnapshot = try {
                itensRef.orderBy("timestamp", Query.Direction.ASCENDING).get(Source.SERVER).await()
            } catch (e: Exception) {
                itensRef.orderBy("timestamp", Query.Direction.ASCENDING).get(Source.CACHE).await()
            }

            val produtos = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val valorField = doc.get("valor")
                    val valor = when (valorField) {
                        is Double -> valorField.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                        is String -> valorField.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
                        else -> BigDecimal.ZERO
                    }

                    val valorTotalField = doc.get("valorTotal")
                    val valorTotal = when (valorTotalField) {
                        is Double -> valorTotalField.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                        is String -> valorTotalField.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
                        else -> BigDecimal.ZERO
                    }

                    Lista(
                        idProduto = doc.id,
                        produto = doc.getString("produto") ?: "",
                        quantidade = (doc.getLong("quantidade") ?: 0L).toInt(),
                        categoria = doc.getString("categoria") ?: "",
                        valor = valor,
                        valorTotal = valorTotal
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
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

    fun adicionarProduto(usuarioUID: String, tituloFeira: String, dados: Map<String, Any>): Result<Unit> {
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
            .get(Source.CACHE) // Para leitura rápida local (pode ser ajustado)

    fun atualizarProduto(usuarioUID: String, tituloFeira: String, idProduto: String, dados: Map<String, Any>): Result<Unit> {
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

        val itensCollectionRef = db.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(nomeFeira)
            .collection("itens")

        val feiraRef = db.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(nomeFeira)

        val idFeiraRef = db.collection("usuarios")
            .document(usuarioUID)
            .collection("idFeiras")
            .document(idFeira)

        return itensCollectionRef.get().continueWithTask { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                querySnapshot?.forEach { itemDoc ->
                    batch.delete(itemDoc.reference)
                }
                batch.delete(feiraRef)
                batch.delete(idFeiraRef)
                batch.commit()
            } else {
                throw task.exception ?: Exception("Erro ao obter itens da feira")
            }
        }
    }

    suspend fun zerarValoresItens(usuarioUID: String, tituloFeira: String): Result<Unit> {
        return try {
            val itensCollectionRef = firebaseStore
                .collection("usuarios")
                .document(usuarioUID)
                .collection("feiras")
                .document(tituloFeira)
                .collection("itens")

            val querySnapshot = try {
                itensCollectionRef.get(Source.SERVER).await()
            } catch (e: Exception) {
                itensCollectionRef.get(Source.CACHE).await()
            }

            querySnapshot.documents.forEach { doc ->
                val itemRef = doc.reference
                itemRef.update(
                    mapOf(
                        "valor" to 0.0,
                        "valorTotal" to 0.0
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementarQuantidade(
        usuarioUID: String,
        tituloFeira: String,
        idProduto: String
    ): Result<Unit> {
        return try {
            val produtoRef = firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("feiras")
                .document(tituloFeira)
                .collection("itens")
                .document(idProduto)

            val snapshot = try {
                produtoRef.get(Source.SERVER).await()
            } catch (e: Exception) {
                produtoRef.get(Source.CACHE).await()
            }

            val quantidadeAtual = snapshot.getLong("quantidade") ?: 0
            val novaQuantidade = quantidadeAtual + 1

            // Pegando valor como String ou Double
            val valorField = snapshot.get("valor")
            val valorUnitario = when (valorField) {
                is Double -> valorField.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                is String -> valorField.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
                else -> BigDecimal.ZERO
            }

            val valorTotal = valorUnitario.multiply(BigDecimal(novaQuantidade)).setScale(2, RoundingMode.HALF_EVEN)

            produtoRef.set(
                mapOf(
                    "quantidade" to novaQuantidade,
                    "valorTotal" to valorTotal.toDouble() // salva como Double
                ), SetOptions.merge()
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun decrementarQuantidade(
        usuarioUID: String,
        tituloFeira: String,
        idProduto: String
    ): Result<Unit> {
        return try {
            val produtoRef = firebaseStore.collection("usuarios")
                .document(usuarioUID)
                .collection("feiras")
                .document(tituloFeira)
                .collection("itens")
                .document(idProduto)

            val snapshot = try {
                produtoRef.get(Source.SERVER).await()
            } catch (e: Exception) {
                produtoRef.get(Source.CACHE).await()
            }

            val quantidadeAtual = snapshot.getLong("quantidade") ?: 0

            if (quantidadeAtual > 0) {
                val novaQuantidade = quantidadeAtual - 1

                // Recupera o campo "valor" como String ou Double
                val valorField = snapshot.get("valor")
                val valorUnitario = when (valorField) {
                    is Double -> valorField.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                    is String -> valorField.toBigDecimalOrNull()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
                    else -> BigDecimal.ZERO
                }

                val valorTotal = valorUnitario.multiply(BigDecimal(novaQuantidade)).setScale(2, RoundingMode.HALF_EVEN)

                produtoRef.set(
                    mapOf(
                        "quantidade" to novaQuantidade,
                        "valorTotal" to valorTotal.toDouble() // Salvar como Double
                    ), SetOptions.merge()
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
