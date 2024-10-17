package com.example.feirafacil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feirafacil.adapter.ListaAdapter
import com.example.feirafacil.databinding.ActivityListaFeiraBinding
import com.example.feirafacil.model.Lista

class ListaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListaFeiraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaFeiraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

    }
}