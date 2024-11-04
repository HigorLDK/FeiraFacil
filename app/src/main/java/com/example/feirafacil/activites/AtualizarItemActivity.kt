package com.example.feirafacil.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feirafacil.databinding.ActivityAtualizarItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AtualizarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAtualizarItemBinding

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var usuarioUID : String

    private lateinit var idRecupeado : String

    private lateinit var tituloFeira : String

    private lateinit var produto : String
    private var quantidade : Int = 0
    private var valor : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        usuarioUID = firebaseAuth.currentUser!!.uid

        val data = intent.getStringExtra("idProduto")
        if (data != null) {
            idRecupeado = data
        }
        val data2 = intent.getStringExtra("tituloFeira")
        if (data2 != null) {
            tituloFeira = data2
        }


        eventosClique()

        }

    override fun onStart() {
        super.onStart()
        recuperarDados()
    }

    private fun recuperarDados() {



        firebaseStore
            .collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(tituloFeira)
            .collection("itens")
            .document(idRecupeado)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val dadosProduto = documentSnapshot.data
                if (dadosProduto != null){

                    val produto = dadosProduto["produto"] as String
                    val quantidade = dadosProduto["quantidade"] as Long
                    val valor = dadosProduto["valor"] as Double

                    binding.editProduto2.setText( produto )
                    binding.editQuant2.setText( quantidade.toString() )
                    binding.editPreco2.setText( valor.toString() )

                }
            }

    }

    private fun eventosClique() {

        binding.btnSalvar.setOnClickListener {

            produto = binding.editProduto2.text.toString()
            quantidade = binding.editQuant2.text.toString().toInt()
            valor = binding.editPreco2.text.toString().toDouble()
            val valorTotal = quantidade * valor

            val dados = mapOf(
                "produto" to produto,
                "quantidade" to quantidade,
                "valor" to valor,
                "valorTotal" to valorTotal
            )

            firebaseStore
            .collection("usuarios")
            .document(usuarioUID)
            .collection("feiras")
            .document(tituloFeira)
            .collection("itens")
            .document(idRecupeado)
            .update(dados)
                .addOnSuccessListener {
                    val intent = Intent(this, NovaFeiraActivity::class.java)
                    intent.putExtra("source", "ActivityAtualizar")
                    intent.putExtra("tituloFeira", tituloFeira)
                    intent.action = "ActivityAtualizar"
                    startActivity(intent)
                    finish()
                }


        }

    }
}

