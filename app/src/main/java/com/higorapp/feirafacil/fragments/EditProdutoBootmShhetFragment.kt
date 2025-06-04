package com.higorapp.feirafacil.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.higorapp.feirafacil.Constants
import com.higorapp.feirafacil.R
import com.higorapp.feirafacil.databinding.AddItemBottomSheetFragmentBinding
import com.higorapp.feirafacil.databinding.EditProdutoBootmShhetFragmentBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.AddItemViewModel
import com.higorapp.feirafacil.viewmodel.AtualizarItemViewModel

class EditProdutoBootmShhetFragment : BottomSheetDialogFragment() {

    private var _binding: EditProdutoBootmShhetFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AtualizarItemViewModel

    private lateinit var tituloFeira: String
    private lateinit var idProduto: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditProdutoBootmShhetFragmentBinding.inflate(inflater, container, false)

        // Inicializar a ViewModel
        viewModel = ViewModelProvider(this, AtualizarItemViewModel.AtualizarItemViewModelFactory(
            FirestoreRepository(),
            FirebaseAuth.getInstance().currentUser!!.uid
        )).get(AtualizarItemViewModel::class.java)

        tituloFeira = arguments?.getString("tituloFeira") ?: ""
        idProduto = arguments?.getString("idProduto") ?: ""

        viewModel.recuperarProduto(tituloFeira, idProduto)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarEventosClique()
        observarDados()

    }

    private fun observarDados() {
        viewModel.produto.observe(this) { produto ->
            produto?.let {
                // Formatar o nome do produto para que a primeira letra de cada palavra fique maiúscula
                val nomeFormatado = produto.produto.lowercase().split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
                binding.editText.setText(nomeFormatado)  // Preenche apenas o nome do produto
            } ?: run {
                Toast.makeText(context, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun inicializarEventosClique() {

        binding.btnSave.setOnClickListener {

            val produto = binding.editText.text.toString()

            if (produto.isNotEmpty()) {  // Apenas validando o nome do produto
                val dados = mapOf(
                    "produto" to produto  // Apenas atualiza o nome do produto
                )
                viewModel.atualizarProduto(tituloFeira, idProduto, dados)  // Atualiza somente o nome do produto
                // Envia um sinal de atualização antes de fechar o fragmento
                val result = Bundle().apply {
                    putBoolean("atualizarLista", true)
                }
                parentFragmentManager.setFragmentResult("atualizarProdutos", result)
                dismiss()
            } else {
                binding.editText.error = "Preencha um produto!"
            }
        }

    }

}


