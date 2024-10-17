package com.example.feirafacil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feirafacil.adapter.FeiraAdapter
import com.example.feirafacil.databinding.ActivityNovaFeiraBinding
import com.example.feirafacil.model.Lista

class NovaFeiraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNovaFeiraBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaFeiraBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}


