package com.higorapp.feirafacil.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.higorapp.feirafacil.adapter.FeiraAdapter
import com.higorapp.feirafacil.databinding.ActivityListaFeiraBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.ListaFeiraViewModel
import com.google.firebase.auth.FirebaseAuth

class ListaFeiraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaFeiraBinding
    private lateinit var feiraAdapter: FeiraAdapter

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var usuarioUID: String? = null

    private lateinit var viewModel: ListaFeiraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaFeiraBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
        supportActionBar?.hide()

        setupToolbar()

        usuarioUID = firebaseAuth.currentUser?.uid
        if (usuarioUID == null) {
            autenticarUsuario()
        } else {
            inicializarViewModel(usuarioUID!!)
        }

        feiraAdapter = FeiraAdapter(
            { feira ->
                val intent = Intent(this, NovaFeiraActivity::class.java).apply {
                    putExtra("source", "ActivityLista")
                    putExtra("tituloFeira", feira.nomeFeira)
                    action = "ActivityListaFeira"
                }
                startActivity(intent)
            },
            { idFeira, nomeFeira ->
                usuarioUID?.let { uid ->
                    viewModel.excluirFeira(uid, idFeira, nomeFeira)
                }
            }
        )

        binding.rvListaFeira.adapter = feiraAdapter
        binding.rvListaFeira.layoutManager = LinearLayoutManager(this)
    }

//    override fun onStart() {
//        super.onStart()
//        setupObservers()
//    }

    private fun autenticarUsuario() {
        firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                usuarioUID = firebaseAuth.currentUser?.uid
                usuarioUID?.let { inicializarViewModel(it) }
            } else {
                Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun inicializarViewModel(uid: String) {
        val viewModelFactory = ListaFeiraViewModel.ListaFeiraViewModelFactory(FirestoreRepository(), uid)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ListaFeiraViewModel::class.java)

        // Inicializamos o ViewModel, depois configuramos os observadores
        setupObservers()

        viewModel.recuperarFeiras()
    }

    private fun setupObservers() {
        viewModel.feiras.observe(this) { feiras ->
            if (feiras.isNullOrEmpty()) {
                // Se não houver feiras, exibe a mensagem "Nenhuma feira disponível" e esconde o RecyclerView
                binding.tvEmptyMessage.visibility = View.VISIBLE
                binding.rvListaFeira.visibility = View.GONE
                //Toast.makeText(this, "Nenhuma feira disponível", Toast.LENGTH_SHORT).show()
                feiraAdapter.adicionarFeira(emptyList())
            } else {
                // Se houver feiras, esconde a mensagem e exibe o RecyclerView
                binding.tvEmptyMessage.visibility = View.GONE
                binding.rvListaFeira.visibility = View.VISIBLE

                feiraAdapter.adicionarFeira(feiras)
            }
        }

        viewModel.exclusaoEstado.observe(this) { result ->
            result.onSuccess {
                Log.i("info_feira", "$result")
                viewModel.recuperarFeiras()
            }.onFailure { error ->
                Toast.makeText(this, "Erro ao excluir: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbListaFeira)
        supportActionBar?.apply {
            title = "Lista de Feiras"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
