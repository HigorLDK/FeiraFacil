package com.higorapp.feirafacil.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.higorapp.feirafacil.R
import com.higorapp.feirafacil.databinding.ActivityMainBinding
import com.higorapp.feirafacil.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    private lateinit var usuarioUID: String
    private var tituloFeira: String = ""

    private var isFeiraSalva = false // Flag para controlar a navegação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
        supportActionBar?.hide()


        if (FirebaseAuth.getInstance().currentUser == null) {
            viewModel.logarAnonimo()
        } else {
            usuarioUID = FirebaseAuth.getInstance().currentUser!!.uid
        }

        eventosClique()



    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

        setupObservers()

    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { result ->
            result.onSuccess { uid ->
                usuarioUID = uid
                Log.i("info_firebase", "Login realizado com sucesso")
                //Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Log.i("info_firebase", "Erro ao realizar o login")
                //Toast.makeText(this, "Erro no login: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.statusFeira.observe(this) { result->

            result.onSuccess {
                if (!isFeiraSalva) { // Verifica se a feira já foi salva
                    //Toast.makeText(this, "Feira salva com sucesso", Toast.LENGTH_LONG).show()
                    isFeiraSalva = true // Marca que a feira foi salva


                    val intent = Intent(this, AddItemActivity::class.java)
                    intent.putExtra("tituloFeira", tituloFeira)
                    startActivity(intent)
                }
            }.onFailure { error ->
                Log.i("info_feira", "Erro feira já existe")
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun eventosClique() {
        binding.btnNF.setOnClickListener {
            val alertBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            val inflater: LayoutInflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editTextTitulo)

            alertBuilder.setTitle("Adicione um título")
            alertBuilder.setPositiveButton("Salvar") { _, _ ->
                tituloFeira = editText.text.toString()
                isFeiraSalva = false // Resetando o flag antes de tentar salvar a feira
                if (tituloFeira.isEmpty()) {
                    Toast.makeText(this,"Digite um título valido", Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.salvarIdFeira(usuarioUID, tituloFeira)
                }

            }
            alertBuilder.setNegativeButton("Cancelar") { _, _ -> }
            alertBuilder.setView(dialogLayout)
            alertBuilder.create().show()
        }

        binding.btnLF.setOnClickListener {
            val intent = Intent(this, ListaFeiraActivity::class.java)
            startActivity(intent)
        }
    }
}
