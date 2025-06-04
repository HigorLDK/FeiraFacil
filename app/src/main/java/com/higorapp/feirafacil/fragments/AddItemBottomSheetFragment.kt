package com.higorapp.feirafacil.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.higorapp.feirafacil.databinding.AddItemBottomSheetFragmentBinding
import com.higorapp.feirafacil.repository.FirestoreRepository
import com.higorapp.feirafacil.viewmodel.AddItemViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class AddItemBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: AddItemBottomSheetFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var tituloFeira: String
    private lateinit var viewModel: AddItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddItemBottomSheetFragmentBinding.inflate(inflater, container, false)

        tituloFeira = arguments?.getString("tituloFeira") ?: ""

        // Inicializar a ViewModel
        viewModel = ViewModelProvider(this, AddItemViewModel.AddItemViewModelFactory(
            FirestoreRepository(),
            FirebaseAuth.getInstance().currentUser!!.uid
        )).get(AddItemViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura o BottomSheet para se comportar corretamente quando o teclado aparecer
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        // Definindo o comportamento para permitir que o bottom sheet se mova para cima quando o teclado aparecer
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isHideable = false

        // Inicializa eventos de clique e o spinner
        inicializaEventosClique()
        spinnerExibicao()

        // Formatar preço no EditText
        formatarPrecoEditText()
    }

    private fun formatarPrecoEditText() {
        val editPreco: EditText = binding.editPreco

        // Adiciona o TextWatcher para formatar o valor como Real
        editPreco.addTextChangedListener(object : TextWatcher {
            var ultimoValor = ""

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val textoAtual = charSequence.toString()

                if (textoAtual != ultimoValor) {
                    editPreco.removeTextChangedListener(this)

                    try {
                        // Remover caracteres não numéricos
                        var valorSemMascara = textoAtual.replace("[^\\d]".toRegex(), "")

                        // Formatar o valor
                        if (valorSemMascara.isNotEmpty()) {
                            val valorFormatado = formatarComoMoeda(valorSemMascara.toDouble())
                            editPreco.setText(valorFormatado)
                            editPreco.setSelection(valorFormatado.length)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    editPreco.addTextChangedListener(this)
                }

                ultimoValor = textoAtual
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun formatarComoMoeda(valor: Double): String {
        val formato = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return formato.format(valor / 100)  // divide por 100 para lidar com os centavos
    }

    private fun spinnerExibicao() {
        val categorias = listOf("Alimentos", "Limpeza", "Higiene", "Outros")
        binding.spinnerCategorias.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )
    }

    private fun inicializaEventosClique() {
        binding.btnAdicionar.setOnClickListener {
            if (validarCampos()) {
                val categoriaSelecionada = binding.spinnerCategorias.selectedItem
                val produto = binding.editProduto.text.toString()
                val quantidade = binding.editQuantidade.text.toString().toInt()

                // Obter o valor como texto, remover R$, pontos e substituir vírgula por ponto
                val valorTexto = binding.editPreco.text.toString()
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim()

                val valor = try {
                    BigDecimal(valorTexto).setScale(2, RoundingMode.HALF_EVEN)
                } catch (e: Exception) {
                    BigDecimal.ZERO
                }

                val tituloRecebido = tituloFeira

                if (tituloRecebido.isNotEmpty()) {
                    viewModel.adicionarItem(
                        tituloRecebido,
                        produto,
                        quantidade,
                        valor,
                        categoriaSelecionada.toString()
                    )

                    val result = Bundle().apply {
                        putBoolean("atualizarLista", true)
                    }
                    parentFragmentManager.setFragmentResult("atualizarProdutos", result)

                    dismiss()
                } else {
                    Toast.makeText(context, "Erro ao salvar produto: título não recebido", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun validarCampos(): Boolean {
        val produto = binding.editProduto.text.toString()
        val quantidadeTexto = binding.editQuantidade.text.toString()

        return when {
            produto.isEmpty() -> {
                binding.editProduto.error = "Preencha um produto!"
                false
            }
            quantidadeTexto.isEmpty() || quantidadeTexto.toIntOrNull() == null || quantidadeTexto.toInt() <= 0 -> {
                binding.editQuantidade.error = "Preencha uma quantidade válida!"
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


