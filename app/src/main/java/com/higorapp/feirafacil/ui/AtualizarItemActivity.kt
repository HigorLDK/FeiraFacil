package com.higorapp.feirafacil.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.higorapp.feirafacil.Constants
import com.higorapp.feirafacil.databinding.ActivityAtualizarItemBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.AtualizarItemViewModel
import com.google.firebase.auth.FirebaseAuth

class AtualizarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAtualizarItemBinding

    private val viewModel: AtualizarItemViewModel by viewModels {
        AtualizarItemViewModel.AtualizarItemViewModelFactory(
            FirestoreRepository(),
            FirebaseAuth.getInstance().currentUser!!.uid
        )
    }

    private lateinit var tituloFeira: String
    private lateinit var idProduto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarItemBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
        supportActionBar?.hide()

        tituloFeira = intent.getStringExtra("tituloFeira") ?: ""
        idProduto = intent.getStringExtra("idProduto") ?: ""

        inicializarEventosClique()
        viewModel.recuperarProduto(tituloFeira, idProduto)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Criar o Intent para navegar para a outra Activity
                val intent = Intent(this@AtualizarItemActivity, NovaFeiraActivity::class.java).apply {
                    putExtra("source", "ActivityAtualizar")
                    putExtra("tituloFeira", tituloFeira)
                }
                startActivity(intent)
                finish()  // Finalizar a MainActivity
            }
        })


        }

    override fun onStart() {
        super.onStart()
        observarDados()
    }

    private fun observarDados() {
        viewModel.produto.observe(this) { produto ->
            produto?.let {
                binding.editProduto2.setText(it.produto)
                binding.editQuant2.setText(it.quantidade.toString())
                binding.editPreco2.setText(it.valor.toString())
            } ?: run {
                Toast.makeText(this, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.atualizacaoEstado.observe(this) { status ->
         if (status == Constants.SUCCESS){
             voltarParaNovaFeira()
         }
        }
    }

    private fun inicializarEventosClique() {
        binding.btnSalvar.setOnClickListener {
            val produto = binding.editProduto2.text.toString()
            val quantidade = binding.editQuant2.text.toString().toIntOrNull()
            val valor = binding.editPreco2.text.toString().toDoubleOrNull()

            if (validarCampos(produto, quantidade, valor)) {
                val dados = mapOf(
                    "produto" to produto,
                    "quantidade" to quantidade!!,
                    "valor" to valor!!,
                    "valorTotal" to (quantidade * valor)
                )
                viewModel.atualizarProduto(tituloFeira, idProduto, dados)
            }
        }
    }

    private fun validarCampos(produto: String, quantidade: Int?, valor: Double?): Boolean {
        return when {
            produto.isEmpty() -> {
                binding.editProduto2.error = "Preencha um produto!"
                false
            }
            quantidade == null || quantidade <= 0 -> {
                binding.editQuant2.error = "Preencha uma quantidade válida!"
                false
            }
            valor == null || valor <= 0.0 -> {
                binding.editPreco2.error = "Preencha um valor válido!"
                false
            }
            else -> true
        }
    }

    private fun voltarParaNovaFeira() {
        val intent = Intent(this, NovaFeiraActivity::class.java).apply {
            putExtra("source", "ActivityAtualizar")
            putExtra("tituloFeira", tituloFeira)
        }
        startActivity(intent)
        finish()
    }


}

