package com.example.feirafacil.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.feirafacil.databinding.ActivityAddItemBinding
import com.example.feirafacil.repository.FirestoreRepository
import com.example.feirafacil.viewmodel.AddItemViewModel
import com.google.firebase.auth.FirebaseAuth

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddItemBinding

    private val viewModel: AddItemViewModel by viewModels {
        AddItemViewModel.AddItemViewModelFactory(
            FirestoreRepository(),
            FirebaseAuth.getInstance().currentUser!!.uid
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
        supportActionBar?.hide()

    }

    override fun onStart() {
        super.onStart()
        inicializarEventosClique()
    }

    private fun inicializarEventosClique() {
        binding.btnAdicionar.setOnClickListener {
            if (validarCampos()) {
                val produto = binding.editProduto.text.toString()
                val quantidade = binding.editQuantidade.text.toString().toInt()
                val valor = binding.editPreco.text.toString().toDouble()
                val tituloRecebido = intent.getStringExtra("tituloFeira")

                if (!tituloRecebido.isNullOrEmpty()) {
                    viewModel.adicionarItem(tituloRecebido, produto, quantidade, valor)
                    voltarParaNovaFeira()
                } else {
                    Toast.makeText(this, "Erro ao salvar produto: título não recebido", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val produto = binding.editProduto.text.toString()
        val quantidadeTexto = binding.editQuantidade.text.toString()

        return when {
            produto.isEmpty() -> {
                binding.editProduto.error = "Preencha um produto!"
                false
            }
            quantidadeTexto.isEmpty() || quantidadeTexto.toIntOrNull() == null || quantidadeTexto.toInt() <= 0 -> {
                binding.editQuantidade.error = "Preencha uma quantidade válida!"
                false
            }
            else -> true
        }
    }

    private fun voltarParaNovaFeira() {
        val tituloRecebido = intent.getStringExtra("tituloFeira")
        val intent = Intent(this, NovaFeiraActivity::class.java).apply {
            putExtra("source", "ActivityItem")
            putExtra("tituloFeira", tituloRecebido)
            action = "ActivityAddItem"
        }
        startActivity(intent)
        finish()
    }
}