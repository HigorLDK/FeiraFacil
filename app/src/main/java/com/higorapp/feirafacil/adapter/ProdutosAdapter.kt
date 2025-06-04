package com.higorapp.feirafacil.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.higorapp.feirafacil.R
import com.higorapp.feirafacil.databinding.ItemLista2Binding
import com.higorapp.feirafacil.model.Lista
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class ProdutosAdapter(
    private val context: Context,
    val onClickExcluir: (String) -> Unit,
    val onClickEditarValor: (String) -> Unit,
    val onClickEditarProduto: (String) -> Unit,
    val onIncrementarClick: (String) -> Unit,
    val onDecrementarClick: (String) -> Unit ) : Adapter<ProdutosAdapter.ProdutosViewHolder>() {

    private var listaProdutos = mutableListOf<Lista>()
    // Atualiza a lista de produtos no Adapter
    fun adicionarLista(novaLista: List<Lista>) {
        Log.d("ADAPTER", "Nova lista recebida: ${novaLista.size} itens")
        listaProdutos.clear()
        listaProdutos.addAll(novaLista)
        notifyDataSetChanged()
    }



    inner class ProdutosViewHolder(
        private val binding : ItemLista2Binding
    ) : ViewHolder(binding.root){

        fun bind( produto : Lista ){

            // Formatação correta do valor para garantir a exibição com vírgula
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR")) // Localização Brasileira
            val valorUnitario = numberFormat.format(produto.valor)
            val valorTotal = numberFormat.format(produto.valorTotal)

            // Formatar o nome do produto para que a primeira letra de cada palavra fique maiúscula
            val nomeFormatado = produto.produto.lowercase().split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

            binding.textProduto.text = nomeFormatado
            binding.textQuantidade.text = produto.quantidade.toString()
            binding.textValor.text = valorTotal
            binding.textCategoria.text = produto.categoria
            binding.textValorUnitario.text = valorUnitario

            binding.textProduto.setOnClickListener {
                onClickEditarProduto(produto.idProduto)
            }

            binding.btnExcluir.setOnClickListener {
                onClickExcluir(produto.idProduto)
            }
            binding.textValor.setOnClickListener {
                onClickEditarValor(produto.idProduto)
            }

            binding.btnAddQuantidade.setOnClickListener {
                onIncrementarClick(produto.idProduto)
            }

            binding.btnSubQuantidade.setOnClickListener {
                onDecrementarClick(produto.idProduto)
            }

            // Definir o ícone baseado na categoria
            val iconResId = when (produto.categoria) {
                "Alimentos" -> R.drawable.borda_inferior
                "Limpeza" -> R.drawable.borda_inferior2
                "Higiene" -> R.drawable.borda_inferior3
                "Outros" -> R.drawable.borda_inferior
                else -> R.drawable.borda_inferior // Default
            }

            binding.imageCategoria.setImageResource(iconResId)

                val text = binding.textValor
            if (produto.valor.compareTo(BigDecimal.ZERO) == 0){

                    val color = ContextCompat.getColor(context, R.color.red)
                    text.setTextColor(color)
                    //textname.setBackgroundColor(color)

                }else{
                    val color = ContextCompat.getColor(context, R.color.text)
                    text.setTextColor(color)
                }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemLista2Binding.inflate(
            inflater,parent,false
        )
        return ProdutosViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listaProdutos.size
    }

    override fun onBindViewHolder(holder: ProdutosViewHolder, position: Int) {
        val produto =listaProdutos[position]
        holder.bind(produto)
    }



}