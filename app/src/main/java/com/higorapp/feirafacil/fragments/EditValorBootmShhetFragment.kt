package com.higorapp.feirafacil.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.higorapp.feirafacil.databinding.EditValorBootmShhetFragmentBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.AtualizarItemViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class EditValorBootmShhetFragment : BottomSheetDialogFragment() {

    private var _binding: EditValorBootmShhetFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AtualizarItemViewModel

    private lateinit var tituloFeira: String
    private lateinit var idProduto: String
    private lateinit var quantidade: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditValorBootmShhetFragmentBinding.inflate(inflater, container, false)

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
        formatarPrecoEditText()
    }

    private fun observarDados() {
        viewModel.produto.observe(viewLifecycleOwner) { produto ->
            produto?.let {
                binding.editText.setText(formatarComoMoeda(it.valor))
                quantidade = it.quantidade.toString()
            } ?: run {
                Toast.makeText(context, "Erro ao carregar preço", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inicializarEventosClique() {
        binding.btnSave.setOnClickListener {
            val valorUnitarioTexto = binding.editText.text.toString()
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()

            if (valorUnitarioTexto.isNotEmpty()) {
                val valor = try {
                    BigDecimal(valorUnitarioTexto).setScale(2, RoundingMode.HALF_EVEN)
                } catch (e: Exception) {
                    BigDecimal.ZERO
                }

                val qtd = try {
                    quantidade.toInt()
                } catch (e: Exception) {
                    0
                }

                val valorTotal = valor.multiply(BigDecimal(qtd)).setScale(2, RoundingMode.HALF_EVEN)

                val dados = mapOf(
                    "valor" to valor.toPlainString(),
                    "valorTotal" to valorTotal.toPlainString()
                )

                viewModel.atualizarProduto(tituloFeira, idProduto, dados)

                val result = Bundle().apply {
                    putBoolean("atualizarLista", true)
                }
                parentFragmentManager.setFragmentResult("atualizarProdutos", result)
                dismiss()
            } else {
                binding.editText.error = "Preencha o preço!"
            }
        }
    }

    private fun formatarPrecoEditText() {
        val editPreco: EditText = binding.editText

        editPreco.addTextChangedListener(object : TextWatcher {
            var isUpdating = false

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true

                val textoAtual = charSequence.toString()
                val valorSemMascara = textoAtual.replace("[^\\d]".toRegex(), "")
                try {
                    if (valorSemMascara.isNotEmpty()) {
                        // Cria BigDecimal com precisão e divide por 100 para representar centavos
                        val valor = BigDecimal(valorSemMascara).setScale(2).divide(BigDecimal(100))

                        // Formata como moeda brasileira
                        val valorFormatado = formatarComoMoeda(valor)

                        // Atualiza o texto e posiciona o cursor corretamente
                        editPreco.setText(valorFormatado)
                        editPreco.setSelection(valorFormatado.length)
                    } else {
                        editPreco.setText("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                isUpdating = false
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    fun formatarComoMoeda(valor: BigDecimal): String {
        val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return formatador.format(valor)
    }
}
