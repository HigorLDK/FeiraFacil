package com.example.feirafacil.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feirafacil.adapter.FeiraAdapter
import com.example.feirafacil.databinding.ActivityListaFeiraBinding
import com.example.feirafacil.model.Feira
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class ListaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListaFeiraBinding

    private lateinit var feiraAdapter: FeiraAdapter

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var usuarioUID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaFeiraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        usuarioUID = firebaseAuth.currentUser!!.uid

            feiraAdapter = FeiraAdapter({ feira ->
                val intent = Intent(this,NovaFeiraActivity::class.java)
                intent.putExtra("source", "ActivityLista")
                intent.putExtra("tituloFeira", feira.nomeFeira)
                intent.action = "ActivityListaFeira"
                startActivity(intent)
            }, { idFeira, nomeFeira ->
                excluirFeira(idFeira, nomeFeira)
            })
            binding.rvListaFeira.adapter = feiraAdapter
            binding.rvListaFeira.layoutManager = LinearLayoutManager(this)

    }

    private fun excluirFeira(idFeira: String, nomeFeira: String) {

        firebaseStore
            .collection("usuarios")
            .document(usuarioUID)
            .collection("idFeiras")
            .document(idFeira)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Feira e itens apagados com sucesso", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao apagar feira: ${erro.message}", Toast.LENGTH_LONG).show()
            }

        val feiraRef = firebaseStore.collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(nomeFeira)

        // Primeiro, apaga todos os itens da subcoleção "itens"
        feiraRef.collection("itens")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = firebaseStore.batch()

                // Para cada documento na subcoleção "itens", adiciona uma operação de deletar no batch
                for (document in querySnapshot.documents) {
                    val itemRef = feiraRef.collection("itens").document(document.id)
                    batch.delete(itemRef)
                }

                // Apaga todos os itens e depois apaga a feira
                batch.commit().addOnSuccessListener {
                    // Após apagar os itens, apaga o documento da feira
                    feiraRef.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Feira e itens apagados com sucesso", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { erro ->
                            Toast.makeText(this, "Erro ao apagar feira: ${erro.message}", Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener { erro ->
                    Toast.makeText(this, "Erro ao apagar itens: ${erro.message}", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao obter itens: ${erro.message}", Toast.LENGTH_LONG).show()
            }
        recuperarDados()

    }

    override fun onStart() {
        super.onStart()
        recuperarDados()

    }

    private fun recuperarDados() {


        firebaseStore
            .collection("usuarios")
            .document(usuarioUID)
            .collection("idFeiras")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->

                val listaFeiras = mutableListOf<Feira>()

                //Recupera os dados que estão dentro dessa querrySnapshot
                val documentos = querySnapshot?.documents
                documentos?.forEach { documentSnapshot ->

                    //Transforma os dados recuperados em Objeto
                    val lista = documentSnapshot.toObject(Feira::class.java)
                    lista?.idFeira = documentSnapshot.id
                    val timestamp = documentSnapshot.getTimestamp("timestamp")
                    if (timestamp != null) {
                        lista?.data = timestamp.toDate()
                    }
                    //Log.i("feirateste", "$date")
                    if (lista != null) {
                        listaFeiras.add(lista)

                    }
                }
                Log.i("testefeira", "$listaFeiras")
                if (listaFeiras.isNotEmpty()) {
                    feiraAdapter.adicionarFeira(listaFeiras)
                }else{
                    feiraAdapter.adicionarFeira(listaFeiras)
                }

            }

        }


    }
