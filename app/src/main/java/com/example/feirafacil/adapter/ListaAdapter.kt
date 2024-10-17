package com.example.feirafacil.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feirafacil.databinding.ListaFeirarvBinding
import com.example.feirafacil.model.Feira

class ListaAdapter(var listaFeira : List<Feira>, context: Context, val onClickAbrirFeira : (Long) -> Unit) : RecyclerView.Adapter<ListaAdapter.ListaViewHolder>() {

    fun adicionarLista(lista : List<Feira>){
        this.listaFeira = lista
        notifyDataSetChanged()
    }

    inner class ListaViewHolder(listaBinding : ListaFeirarvBinding) : RecyclerView.ViewHolder(listaBinding.root) {

        private val binding: ListaFeirarvBinding = listaBinding

        fun bind(lista : Feira){

            binding.textTituloFeira.text = lista.tituloFeira
            binding.imageCardView.setOnClickListener {
                onClickAbrirFeira(lista.idFeira)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val listaBinding = ListaFeirarvBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ListaViewHolder(listaBinding)
    }

    override fun getItemCount(): Int {
        return listaFeira.size
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val newList = listaFeira[position]
        holder.bind(newList)    }

}