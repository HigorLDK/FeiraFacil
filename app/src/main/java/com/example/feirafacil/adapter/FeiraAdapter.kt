package com.example.feirafacil.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feirafacil.databinding.ItemListaBinding
import com.example.feirafacil.model.Lista

class FeiraAdapter(var listaProdutos : List<Lista>, context : Context, val onClickEditar : (Lista) -> Unit, val onClickExcluir: (Int) -> Unit) : RecyclerView.Adapter<FeiraAdapter.ViewHolder>() {

    //private var listaProdutos: List<Lista> = emptyList()

    fun adicionarLista(lista : List<Lista>){
        this.listaProdutos = lista
        Log.i("info_db", "$listaProdutos")

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemBinding : ItemListaBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        private val binding: ItemListaBinding = itemBinding

        fun bind(lista : Lista){

            binding.textPoduto.text = lista.itemProduto
            binding.textQuantidade.text = lista.quantProduto.toString()
            binding.textPreco.text = "R$ "+lista.precoProduto.toString()
            binding.btnEditar.setOnClickListener {
                    onClickEditar(lista)

            }
            binding.btnExluir.setOnClickListener {
                onClickExcluir(lista.idProduto)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemListaBinding = ItemListaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemListaBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newList = listaProdutos[position]
        holder.bind(newList)


    }

    override fun getItemCount(): Int {
        return  listaProdutos.size
    }
}
