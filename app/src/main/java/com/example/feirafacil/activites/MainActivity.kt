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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        if(firebaseAuth.currentUser == null){
            logarAnonimo()
        }else{
            usuarioUID = firebaseAuth.currentUser!!.uid
            val currentUser = firebaseAuth.currentUser
            currentUser?.let {
                val uid = it.uid
                Log.i("uiduser", "$uid")
            }
        }

        eventosClique()

    }

    private fun logarAnonimo() {

        firebaseAuth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sucesso ao realizar login", Toast.LENGTH_SHORT).show()
                    // Login anônimo bem-sucedido, inicializa usuarioUID
                    val user = firebaseAuth.currentUser
                    user?.let {
                        usuarioUID = it.uid
                        Log.i("uiduser", "$usuarioUID")
                        // Agora o UID pode ser usado
                    }
                } else {
                    task.exception?.let {
                        Toast.makeText(this, "Erro ao realizar login anônimo: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
                tituloFeira = titulo

                salvarIdFeira(titulo)

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

    private fun salvarIdFeira(nomeFeira : String) {

        // Verifica se já existe uma feira com o mesmo título
        firebaseStore.collection("usuarios")
            .document(usuarioUID)
            .collection("idFeiras")
            .whereEqualTo("idFeira", nomeFeira)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Feira com esse título já existe
                    Toast.makeText(this, "Uma feira com esse título já existe!", Toast.LENGTH_SHORT).show()
                } else {
                    // Não existe feira com esse título, prosseguir com a criação
                    val dados = mapOf(
                        "nomeFeira" to nomeFeira,
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    firebaseStore.collection("usuarios")
                        .document(usuarioUID)
                        .collection("idFeiras")
                        .add(dados)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Feira salva com sucesso", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener { erro ->
                            Toast.makeText(this, "Erro ao salvar feira: ${erro.message}", Toast.LENGTH_LONG).show()
                        }

                    val intent = Intent(this,AddItemActivity::class.java)
                    intent.putExtra("tituloFeira", tituloFeira)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro ao verificar feira: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

}
