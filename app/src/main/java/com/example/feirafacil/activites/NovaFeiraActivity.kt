package com.example.feirafacil.activites

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feirafacil.R
import com.example.feirafacil.adapter.ProdutosAdapter
import com.example.feirafacil.databinding.ActivityNovaFeiraBinding
import com.example.feirafacil.model.Feira
import com.example.feirafacil.model.Lista
import com.google.firebase.firestore.FirebaseFirestore

class NovaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNovaFeiraBinding

    private lateinit var produtosAdapter: ProdutosAdapter

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var tituloFeira : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaFeiraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventosClique()

        produtosAdapter = ProdutosAdapter()
        binding.rvFeira.adapter = produtosAdapter
        binding.rvFeira.layoutManager = LinearLayoutManager(this)
        binding.rvFeira.addItemDecoration(
            DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL
            )
        )

    }

    override fun onStart() {
        super.onStart()
        recuperarDados()

    }


    private fun recuperarDados() {


        val extras = intent.extras
        if (extras != null) {
            val titulo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("tituloFeira", Feira::class.java)
            } else {
                extras.getParcelable("tituloFeira")
            }
            if (titulo != null) {

                tituloFeira = titulo.idFeira
                firebaseStore
                    .collection(tituloFeira)
                    .addSnapshotListener { querySnapshot, error ->

                        val listaProdutos = mutableListOf<Lista>()

                        val listaCamposEspecificos = mutableListOf<String>()
                        //Recupera os dados que estão dentro dessa querrySnapshot
                        val documentos = querySnapshot?.documents
                        documentos?.forEach { documentSnapshot ->

                            //Transforma os dados recuperados em Objeto
                            val lista = documentSnapshot.toObject(Lista::class.java)
                            if (lista != null) {
                                listaProdutos.add(lista)


                                listaCamposEspecificos.add(lista.idFeira)
                                //Log.i("teste", "$listaCamposEspecificos")
                                //Pega todos os valoresTotais e soma
                                val valores = listaProdutos.sumOf { it.valorTotal }
                                binding.textValorTotal.text = valores.toString()
                            }
                        }

                        if (listaProdutos.isNotEmpty()) {
                            produtosAdapter.adicionarLista(listaProdutos)
                        }

                    }
            }
        }

            val tituloRecebido = intent.getStringExtra("tituloFeira")
            if (tituloRecebido != null) {
                tituloFeira = tituloRecebido


                firebaseStore
                    .collection(tituloFeira)
                    .addSnapshotListener { querySnapshot, error ->

                        val listaProdutos = mutableListOf<Lista>()

                        val listaCamposEspecificos = mutableListOf<String>()
                        //Recupera os dados que estão dentro dessa querrySnapshot
                        val documentos = querySnapshot?.documents
                        documentos?.forEach { documentSnapshot ->

                            //Transforma os dados recuperados em Objeto
                            val lista = documentSnapshot.toObject(Lista::class.java)
                            if (lista != null) {
                                listaProdutos.add(lista)


                                listaCamposEspecificos.add(lista.idFeira)
                                //Log.i("teste", "$listaCamposEspecificos")
                                //Pega todos os valoresTotais e soma
                                val valores = listaProdutos.sumOf { it.valorTotal }
                                binding.textValorTotal.text = valores.toString()
                            }
                        }

                        if (listaProdutos.isNotEmpty()) {
                            produtosAdapter.adicionarLista(listaProdutos)
                        }

                    }


            }
    }


    private fun eventosClique() {

        binding.btnAdd.setOnClickListener {

            when (intent.action) {
                "ActivityListaFeira" -> {
                    val intent = Intent(this,AddItemActivity::class.java)
                    intent.putExtra("tituloFeira", tituloFeira)
                    startActivity(intent)
                }
                "ActivityAddItem" -> {
                    val intent = Intent(this,AddItemActivity::class.java)
                    intent.putExtra("tituloFeira", tituloFeira)
                    startActivity(intent)
                }
            }


        }

    }

}


