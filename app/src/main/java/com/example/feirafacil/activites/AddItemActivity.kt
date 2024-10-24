package com.example.feirafacil.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feirafacil.databinding.ActivityAddItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddItemBinding

    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var produto : String
    private var quantidade : Int = 0
    private var valor : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        inicializarEventosClique()



    }

    private fun inicializarEventosClique() {

        binding.btnAdicionar.setOnClickListener {

            if (validarCampos()) {

                produto = binding.editProduto.text.toString()
                quantidade = binding.editQuantidade.text.toString().toInt()
                valor = binding.editPreco.text.toString().toDouble()
                val valorTotal = quantidade * valor

                /*val dados = mapOf(
                    "produto" to produto,
                    "quantidade" to quantidade,
                    "valor" to valor,
                    "valorTotal" to valorTotal

                )*/

                val tituloRecebido = intent.getStringExtra("tituloFeira")
                if (tituloRecebido != null) {

                    val dados = mapOf(
                        "produto" to produto,
                        "quantidade" to quantidade,
                        "valor" to valor,
                        "valorTotal" to valorTotal,
                        "idFeira" to tituloRecebido

                    )

                    firebaseStore.collection(tituloRecebido)
                        .add(dados)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Sucesso ao salvar produto", Toast.LENGTH_LONG)
                                .show()
                        }.addOnFailureListener { erro ->
                            Toast.makeText(this, "Erro ao salvar produto", Toast.LENGTH_LONG).show()
                        }

                    val intent = Intent(this, NovaFeiraActivity::class.java)
                    intent.putExtra("tituloFeira", tituloRecebido)
                    intent.action = "ActivityAddItem"
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "erro ao salvar produto", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validarCampos(): Boolean {

        produto = binding.editProduto.text.toString()
        val quantidadeTexto = binding.editQuantidade.text.toString()
        val valorTexto = binding.editPreco.text.toString()


        if (produto.isNotEmpty()){
            binding.editProduto.error = null
            if ( quantidadeTexto.isNotEmpty() ){
                //Devemos converter um valor para Int primeiro
                quantidade = quantidadeTexto.toIntOrNull()!!
                if (quantidade > 0) {
                    binding.editQuantidade.error = null
                }
                if ( valorTexto.isNotEmpty() ){
                    valor = valorTexto.toDoubleOrNull()!!
                    if (valor > 0){
                        binding.editPreco.error = null
                    }
                    return true
                }else{
                    binding.editPreco.error = "Preencha um valor!"
                    return false
                }
            }else{
                    binding.editQuantidade.error = "Preencha a quantidade!"
                    return false
            }
        }else{
            binding.editProduto.error = "Preencha um produto!"
            return false
        }



    }
}