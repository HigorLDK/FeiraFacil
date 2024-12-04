package com.example.feirafacil.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.feirafacil.databinding.ListaFeirarvBinding
import com.example.feirafacil.model.Feira
import java.text.SimpleDateFormat
import java.util.Locale

class FeiraAdapter(private val onClick: (Feira) -> Unit, private val onClickExcluir : (String, String) -> Unit) : Adapter<FeiraAdapter.FeiraViewHolder>() {

    private var listaFeira = mutableListOf<Feira>()
    fun adicionarFeira(lista: List<Feira>){
        listaFeira = lista.toMutableList()
        notifyDataSetChanged()
    }


    inner class FeiraViewHolder(private val binding : ListaFeirarvBinding) : ViewHolder(binding.root){

        fun bind(feira : Feira){

            binding.textTituloFeira.text = feira.nomeFeira

            binding.clFeira.setOnClickListener {
                onClick(feira)
            }

            binding.btnExcluirFeira.setOnClickListener {
               onClickExcluir(feira.idFeira, feira.nomeFeira)
            }

            feira.data?.let { date ->

                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val dataFormatada = dateFormat.format(date)

                binding.textDataFeira.text = dataFormatada

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeiraViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ListaFeirarvBinding.inflate(
            inflater,parent,false
        )
        return FeiraViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listaFeira.size
    }

    override fun onBindViewHolder(holder: FeiraViewHolder, position: Int) {
        val feira = listaFeira[position]
        holder.bind(feira)
    }

}