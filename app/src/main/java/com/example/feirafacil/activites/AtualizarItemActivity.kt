package com.example.feirafacil.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.feirafacil.databinding.ActivityAtualizarItemBinding

class AtualizarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAtualizarItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        }
    }

