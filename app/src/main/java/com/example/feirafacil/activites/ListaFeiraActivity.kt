package com.example.feirafacil.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feirafacil.adapter.FeiraAdapter
import com.example.feirafacil.databinding.ActivityListaFeiraBinding
import com.example.feirafacil.model.Feira
import com.google.firebase.firestore.FirebaseFirestore

class ListaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListaFeiraBinding

    private lateinit var feiraAdapter: FeiraAdapter

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaFeiraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


            feiraAdapter = FeiraAdapter{ feira ->
                val intent = Intent(this,NovaFeiraActivity::class.java)
                intent.putExtra("tituloFeira", feira)
                intent.action = "ActivityListaFeira"
                startActivity(intent)
            }
            binding.rvListaFeira.adapter = feiraAdapter
            binding.rvListaFeira.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        recuperarDados()

    }

    private fun recuperarDados() {


        firebaseStore
            .collection("idFeiras")
            .addSnapshotListener { querySnapshot, error ->

                val listaFeiras = mutableListOf<Feira>()

                //Recupera os dados que estão dentro dessa querrySnapshot
                val documentos = querySnapshot?.documents
                documentos?.forEach { documentSnapshot ->

                    //Transforma os dados recuperados em Objeto
                    val lista = documentSnapshot.toObject(Feira::class.java)
                    if (lista != null) {
                        listaFeiras.add(lista)

                    }
                }

                if (listaFeiras.isNotEmpty()) {
                    feiraAdapter.adicionarFeira(listaFeiras)
                }

            }

        }
    }
