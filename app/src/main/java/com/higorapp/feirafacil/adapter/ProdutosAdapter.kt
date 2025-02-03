package com.higorapp.feirafacil.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.higorapp.feirafacil.R
import com.higorapp.feirafacil.databinding.ItemListaBinding
import com.higorapp.feirafacil.model.Lista
import java.text.NumberFormat
import java.util.Locale

class ProdutosAdapter(private val context: Context, val onClickExcluir: (String) -> Unit, val onClickEditar: (String) -> Unit ) : Adapter<ProdutosAdapter.ProdutosViewHolder>() {

    private var listaProdutos = mutableListOf<Lista>()
    // Atualiza a lista de produtos no Adapter
    fun adicionarLista(novaLista: List<Lista>) {
        listaProdutos.clear()
        listaProdutos.addAll(novaLista)
        notifyDataSetChanged()
    }



    inner class ProdutosViewHolder(
        private val binding : ItemListaBinding
    ) : ViewHolder(binding.root){

        fun bind( produto : Lista ){

            // Formatação correta do valor para garantir a exibição com vírgula
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR")) // Localização Brasileira
            val valorFormatado = numberFormat.format(produto.valor)

            binding.textProduto.text = produto.produto
            binding.textQuantidade.text = produto.quantidade.toString()
            binding.textValor.text = valorFormatado

            binding.btnExcluir.setOnClickListener {
                onClickExcluir(produto.idProduto)
            }
            binding.btnEditar.setOnClickListener {
                onClickEditar(produto.idProduto)
            }


                val textname = binding.textProduto
                val text = binding.textValor

                if (produto.valor == 0.0){

                    val color = ContextCompat.getColor(context, R.color.red)
                    text.setBackgroundColor(color)
                    textname.setBackgroundColor(color)

                }else {
                    // Definir cor padrão (preta ou outra) quando o valor é maior que 0
                    val colorDefault = ContextCompat.getColor(context, R.color.colorDefault) // ou outra cor padrão
                    text.setBackgroundColor(colorDefault)
                    textname.setBackgroundColor(colorDefault)
                }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemListaBinding.inflate(
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