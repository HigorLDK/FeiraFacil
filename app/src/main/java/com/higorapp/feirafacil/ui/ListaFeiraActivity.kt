package com.higorapp.feirafacil.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.higorapp.feirafacil.adapter.FeiraAdapter
import com.higorapp.feirafacil.databinding.ActivityListaFeiraBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.ListaFeiraViewModel
import com.google.firebase.auth.FirebaseAuth

class ListaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListaFeiraBinding

    private lateinit var feiraAdapter: FeiraAdapter

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val usuarioUID: String by lazy { firebaseAuth.currentUser!!.uid }

    private val viewModel: ListaFeiraViewModel by viewModels {
        ListaFeiraViewModel.ListaFeiraViewModelFactory(FirestoreRepository(), usuarioUID)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaFeiraBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
        supportActionBar?.hide()


        feiraAdapter = FeiraAdapter(
            { feira ->
                val intent = Intent(this, NovaFeiraActivity::class.java).apply {
                    putExtra("source", "ActivityLista")
                    putExtra("tituloFeira", feira.nomeFeira)
                    action = "ActivityListaFeira"
                }
                startActivity(intent)
            },
            { idFeira, nomeFeira -> viewModel.excluirFeira(usuarioUID, idFeira, nomeFeira) }
        )
        binding.rvListaFeira.adapter = feiraAdapter
        binding.rvListaFeira.layoutManager = LinearLayoutManager(this)

        viewModel.recuperarFeiras()

    }

    override fun onStart() {
        super.onStart()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.feiras.observe(this) { feiras ->
            feiraAdapter.adicionarFeira(feiras)
        }

        viewModel.exclusaoEstado.observe(this) { result ->
            result.onSuccess {
                Log.i("info_feira", "$result")
                //Toast.makeText(this, "Feira e itens excluídos com sucesso", Toast.LENGTH_LONG).show()
            }.onFailure { error ->
                Toast.makeText(this, "Erro ao excluir: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
