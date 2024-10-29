package com.example.feirafacil.activites

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feirafacil.adapter.ProdutosAdapter
import com.example.feirafacil.databinding.ActivityNovaFeiraBinding
import com.example.feirafacil.model.Feira
import com.example.feirafacil.model.Lista
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NovaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNovaFeiraBinding

    private lateinit var produtosAdapter: ProdutosAdapter

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var usuarioUID : String

    private lateinit var tituloFeira : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaFeiraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioUID = firebaseAuth.currentUser!!.uid

        eventosClique()

        produtosAdapter = ProdutosAdapter (
            { documentId ->
            confirmarExclusao(documentId)
        },{ idProduto ->
                val intent = Intent(this, AtualizarItemActivity::class.java)
                intent.putExtra("idProduto", idProduto)
                intent.putExtra("tituloFeira", tituloFeira)
                startActivity(intent)

        })
        binding.rvFeira.adapter = produtosAdapter
        binding.rvFeira.layoutManager = LinearLayoutManager(this)
        binding.rvFeira.addItemDecoration(
            DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL
            )
        )

        val intent = intent
        val source = intent.getStringExtra("source")
        val data = intent.getStringExtra("tituloFeira")

        // Verificar de qual Activity veio a Intent
        if (source == "ActivityLista") {
            // Dados vieram da ActivityLista
            Log.i("testefeira", "ActivityLista = $data")
            if (data != null) {
                tituloFeira = data
            }
        } else if (source == "ActivityItem") {
            // Dados vieram da ActivityItem
            Log.i("testefeira", "ActivityItem = $data")
            if (data != null) {
                tituloFeira = data
            }
        } else if (source == "ActivityAtualizar") {
            // Dados vieram da ActivityAtualizar
            Log.i("testefeira", "ActivityItem = $data")
            if (data != null) {
                tituloFeira = data
            }
        }

        inicializarToolbar()
    }


    override fun onStart() {
        super.onStart()
        recuperarDados()

    }

    private fun recuperarDados() {


                firebaseStore.collection("usuarios")
                    .document(usuarioUID)
                    .collection("feiras")
                    .document(tituloFeira)
                    .collection("itens")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { querySnapshot, error ->


                        val listaProdutos = mutableListOf<Lista>()

                        val listaCamposEspecificos = mutableListOf<String>()
                        //Recupera os dados que estão dentro dessa querrySnapshot
                        val documentos = querySnapshot?.documents
                        documentos?.forEach { documentSnapshot ->

                            //Transforma os dados recuperados em Objeto
                            val lista = documentSnapshot.toObject(Lista::class.java)
                            lista?.idProduto = documentSnapshot.id
                            if (lista != null) {
                                listaProdutos.add(lista)


                                listaCamposEspecificos.add(lista.idFeira)

                                //Pega todos os valoresTotais e soma
                                val valores = listaProdutos.sumOf { it.valorTotal }
                                binding.textValorTotal.text = String.format("R$ %,.2f", valores)
                            }
                        }

                        if (listaProdutos.isNotEmpty()) {
                            produtosAdapter.adicionarLista(listaProdutos)
                        }else{
                            produtosAdapter.adicionarLista(listaProdutos)
                            binding.textValorTotal.text = "R$ 0.000,00"
                        }

                    }

    }

    private fun confirmarExclusao(documentId: String) {


            val alertBuilder = AlertDialog.Builder(this)

            alertBuilder.setTitle("Confirmar Exclusão")
            alertBuilder.setMessage("Deseja realmente excluir o produto?")

            alertBuilder.setPositiveButton("Sim") { _, _ ->


                    firebaseStore
                        .collection("usuarios")
                        .document(usuarioUID)
                        .collection("feiras")
                        .document(tituloFeira)
                        .collection("itens")
                        .document(documentId)
                        .delete()

                recuperarDados()

            }
            alertBuilder.setNegativeButton("Não") { _, _ -> }

            alertBuilder.create().show()

    }

    private fun eventosClique() {

        binding.btnAdd.setOnClickListener {

            when (intent.action) {
                "ActivityListaFeira" -> {
                    val intent = Intent(this,AddItemActivity::class.java)
                    intent.putExtra("tituloFeira", tituloFeira)
                    startActivity(intent)
                    finish()
                }
                "ActivityAddItem" -> {
                    val intent = Intent(this,AddItemActivity::class.java)
                    intent.putExtra("tituloFeira", tituloFeira)
                    startActivity(intent)
                    finish()
                }
            }

        }

    }

    private fun inicializarToolbar() {

        val toolbar = binding.tbNovaFeira
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            binding.textTituloNF.text = tituloFeira
            setDisplayHomeAsUpEnabled(true)
        }

    }

}


