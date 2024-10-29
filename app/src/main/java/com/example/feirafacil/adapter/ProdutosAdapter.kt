package com.example.feirafacil.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.feirafacil.databinding.ItemListaBinding
import com.example.feirafacil.model.Lista

class ProdutosAdapter( val onClickExcluir: (String) -> Unit, val onClickEditar: (String) -> Unit ) : Adapter<ProdutosAdapter.ProdutosViewHolder>() {

    private var listaProdutos = mutableListOf<Lista>()
    fun adicionarLista( lista: MutableList<Lista>){
        listaProdutos = lista

        notifyDataSetChanged()
    }



    inner class ProdutosViewHolder(
        private val binding : ItemListaBinding
    ) : ViewHolder(binding.root){

        fun bind( produto : Lista ){

            binding.textProduto.text = produto.produto
            binding.textQuantidade.text = produto.quantidade.toString()
            binding.textValor.text = "R$ %.2f".format(produto.valor)

            binding.btnExcluir.setOnClickListener {
                onClickExcluir(produto.idProduto)
            }
            binding.btnEditar.setOnClickListener {
                onClickEditar(produto.idProduto)
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