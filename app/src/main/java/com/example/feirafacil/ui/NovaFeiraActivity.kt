package com.example.feirafacil.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feirafacil.R
import com.example.feirafacil.adapter.ProdutosAdapter
import com.example.feirafacil.databinding.ActivityNovaFeiraBinding
import com.example.feirafacil.repository.FirestoreRepository
import com.example.feirafacil.viewmodel.NovaFeiraViewModel
import com.google.firebase.auth.FirebaseAuth


class NovaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNovaFeiraBinding

    private lateinit var produtosAdapter: ProdutosAdapter

    private lateinit var tituloFeira: String

    private val viewModel: NovaFeiraViewModel by viewModels {
        NovaFeiraViewModel.provideFactory(
            FirestoreRepository(),
            FirebaseAuth.getInstance().currentUser!!.uid,
            intent.getStringExtra("tituloFeira") ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaFeiraBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        tituloFeira = intent.getStringExtra("tituloFeira") ?: ""
        eventosClique()

    }


    override fun onStart() {
        super.onStart()

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        viewModel.recuperarProdutos()

    }

    private fun setupRecyclerView() {
        produtosAdapter = ProdutosAdapter(context = this,
            { documentId -> confirmarExclusao(documentId) },
            { idProduto ->
                val intent = Intent(this, AtualizarItemActivity::class.java).apply {
                    putExtra("idProduto", idProduto)
                    putExtra("tituloFeira", tituloFeira)
                }
                startActivity(intent)
                finish()
            }
        )
        binding.rvFeira.apply {
            adapter = produtosAdapter
            layoutManager = LinearLayoutManager(this@NovaFeiraActivity)
            addItemDecoration(DividerItemDecoration(this@NovaFeiraActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewModel.produtos.observe(this) { produtos ->
            produtosAdapter.adicionarLista(produtos)
        }

        viewModel.valorTotal.observe(this) { valorTotal ->
            binding.textValorTotal.text = String.format("R$ %,.2f", valorTotal)
        }

        viewModel.exclusaoEstado.observe(this) { result ->
            result.onFailure {
                Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarExclusao(documentId: String) {
        AlertDialog.Builder(this, R.style.CustomAlertDialog).apply {
            setTitle("Confirmar Exclusão")
            setMessage("Deseja realmente excluir o produto?")
            setPositiveButton("Sim") { _, _ -> viewModel.excluirProduto(documentId) }
            setNegativeButton("Não", null)
        }.show()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbNovaFeira)
        supportActionBar?.apply {
            title = ""
            binding.textTituloNF.text = intent.getStringExtra("tituloFeira")
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun eventosClique() {

        binding.btnAdd.setOnClickListener {

            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("tituloFeira", tituloFeira)
            startActivity(intent)
            finish()

        }

    }

}


