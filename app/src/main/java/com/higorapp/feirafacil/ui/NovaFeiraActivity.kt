package com.higorapp.feirafacil.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.higorapp.feirafacil.R
import com.higorapp.feirafacil.adapter.ProdutosAdapter
import com.higorapp.feirafacil.databinding.ActivityNovaFeiraBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.NovaFeiraViewModel
import com.google.firebase.auth.FirebaseAuth
import com.higorapp.feirafacil.fragments.AddItemBottomSheetFragment
import com.higorapp.feirafacil.fragments.EditValorBootmShhetFragment
import com.higorapp.feirafacil.fragments.EditProdutoBootmShhetFragment



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

        Log.i("testefrag","onCreate")

        supportFragmentManager.setFragmentResultListener("atualizarProdutos", this) { _, bundle ->
            if (bundle.getBoolean("atualizarLista")) {
                viewModel.recuperarProdutos() // Atualiza a lista
            }
        }

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
                val bottomSheetFragment = EditValorBootmShhetFragment().apply {
                    arguments = Bundle().apply {
                        putString("tituloFeira", tituloFeira)
                        putString("idProduto", idProduto)
                    }
                }
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            },{ idProduto ->
                val bottomSheetFragment = EditProdutoBootmShhetFragment().apply {
                    arguments = Bundle().apply {
                        putString("tituloFeira", tituloFeira)
                        putString("idProduto", idProduto)
                    }
                }
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            },{ idProduto->
                viewModel.incrementarQuantidade(tituloFeira,idProduto)
            },{idProduto ->
                viewModel.decrementarQuantidade(tituloFeira,idProduto)
            }
        )
        binding.rvFeira.apply {
            adapter = produtosAdapter
            layoutManager = LinearLayoutManager(this@NovaFeiraActivity)
        }
    }

    private fun setupObservers() {
        viewModel.produtos.observe(this) { produtos ->

            // Ordena os produtos de acordo com a categoria
            val categoriasOrdenadas = listOf("Alimentos", "Limpeza", "Higiene", "Outros")
            val produtosOrdenados = produtos.sortedBy { categoriasOrdenadas.indexOf(it.categoria ?: "Outros") }


            produtosAdapter.adicionarLista(produtosOrdenados)
        }

        viewModel.valorTotal.observe(this) { valorTotal ->
            Log.i("ValorTotal", "Valor total atualizado: $valorTotal")
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

            val bottomSheetFragment = AddItemBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("tituloFeira", tituloFeira)
                }
            }
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)



        }

        binding.btnZerar.setOnClickListener {
           val usuarioUID =  FirebaseAuth.getInstance().currentUser!!.uid
            // Criar o AlertDialog

            AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .setTitle("Zerar Preços")
                .setMessage("Tem certeza que deseja zerar todos os preços dos itens?")
                .setPositiveButton("Sim") { _, _ ->
                    // Chama a função para zerar os valores e recarregar os produtos
                    viewModel.zerarValoresItens(usuarioUID, tituloFeira)
                }
                .setNegativeButton("Não", null) // Não faz nada se "Não" for clicado
                .show()


        }

    }



}


