package com.example.feirafacil.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.feirafacil.R
import com.example.feirafacil.adapter.FeiraAdapter
import com.example.feirafacil.databinding.ActivityMainBinding
import com.example.feirafacil.model.Feira
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        eventosClique()


    }

    override fun onStart() {
        super.onStart()
    }

    private fun eventosClique() {

        binding.btnNF.setOnClickListener {

            val alertBuilder = AlertDialog.Builder(this)
            val inflater : LayoutInflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.edit_text_layout,null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editTextTitulo)

            alertBuilder.setTitle("Adicione um titulo")

            alertBuilder.setPositiveButton("Salvar"){_,_ ->
                //capturo o titulo da feira
                val titulo = editText.text.toString()

                salvarIdFeira(titulo)


                //passa o titulo para proxima activity
                val intent = Intent(this,AddItemActivity::class.java)
                intent.putExtra("tituloFeira", titulo)
                startActivity(intent)


            }
            alertBuilder.setNegativeButton("Cancelar"){_,_ -> }
            alertBuilder.setView(dialogLayout)
            alertBuilder.create().show()
        }

        binding.btnLF.setOnClickListener {

            val intent = Intent(this, ListaFeiraActivity::class.java)
            startActivity(intent)

        }

    }

    private fun salvarIdFeira(idFeira : String) {

        val dados = mapOf(
            "idFeira" to idFeira
        )

        firebaseStore.collection("idFeiras")
            .add(dados)
            .addOnSuccessListener {
                Toast.makeText(this, "Sucesso ao salvar produto", Toast.LENGTH_LONG)
                    .show()
            }.addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao salvar produto", Toast.LENGTH_LONG).show()
            }
    }

}
