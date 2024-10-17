package com.example.feirafacil

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feirafacil.databinding.ActivityAddItemBinding
import com.example.feirafacil.model.Lista

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddItemBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

    }
}