package com.example.feirafacil

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feirafacil.databinding.ActivityAtualizarItemBinding
import com.example.feirafacil.model.Lista

class AtualizarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAtualizarItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        }
    }

